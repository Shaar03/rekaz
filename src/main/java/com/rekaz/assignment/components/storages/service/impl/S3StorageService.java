package com.rekaz.assignment.components.storages.service.impl;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;
import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.blobs.repository.BlobMetadataRepo;
import com.rekaz.assignment.components.storages.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

@Service("s3StorageService")
public class S3StorageService implements StorageService {

    @Value("${S3_ENDPOINT}")
    private String s3Endpoint;

    @Value("${S3_ACCESS_KEY}")
    private String accessKey;

    @Value("${S3_SECRET_KEY}")
    private String secretKey;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;

    private final BlobMetadataRepo blobMetadataRepo;

    public S3StorageService(BlobMetadataRepo blobMetadataRepo) {
        this.blobMetadataRepo = blobMetadataRepo;
    }

    @Override
    public void save(String id, String data) {
        try {
            byte[] decodedData = Base64.getDecoder().decode(data);

            String url = UriComponentsBuilder.fromUriString(s3Endpoint)
                    .pathSegment(bucketName)
                    .pathSegment(id)
                    .toUriString();

            URI uri = new URI(url);

            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);

            String xAmzDate = ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
            String payloadHash = hash(new String(decodedData, StandardCharsets.UTF_8));
            String authHeader = createAuthorizationHeader("PUT", id, xAmzDate, payloadHash);

            connection.setRequestProperty("x-amz-date", xAmzDate);
            connection.setRequestProperty("x-amz-content-sha256", payloadHash);
            connection.setRequestProperty("Authorization", authHeader);

            connection.getOutputStream().write(decodedData);
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                throw new RuntimeException("Failed to save blob to S3. Response code: " + responseCode);
            }

            BlobMetadata metadata = new BlobMetadata();
            metadata.setId(id);
            metadata.setStorageType(StorageTypeEnum.S3);
            metadata.setSize(decodedData.length);
            metadata.setCreatedAt(new Date());
            blobMetadataRepo.save(metadata);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to save blob to S3: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BlobResponse retrieve(String id) {
        try {
            BlobMetadata metadata = blobMetadataRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blob metadata not found for id: " + id));

            String url = UriComponentsBuilder.fromUriString(s3Endpoint)
                    .pathSegment(bucketName)
                    .pathSegment(id)
                    .toUriString();

            URI uri = new URI(url);

            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            String xAmzDate = ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
            String authHeader = createAuthorizationHeader("GET", id, xAmzDate, "UNSIGNED-PAYLOAD");

            connection.setRequestProperty("x-amz-date", xAmzDate);
            connection.setRequestProperty("x-amz-content-sha256", "UNSIGNED-PAYLOAD");
            connection.setRequestProperty("Authorization", authHeader);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                throw new RuntimeException("Failed to retrieve blob from S3. Response code: " + responseCode);
            }

            byte[] fileData = connection.getInputStream().readAllBytes();

            String encodedData = Base64.getEncoder().encodeToString(fileData);

            return new BlobResponse(id, encodedData, metadata.getSize(), metadata.getCreatedAt());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to retrieve blob from S3: " + e.getMessage(), e);
        }
    }

    private String createAuthorizationHeader(String method, String key, String xAmzDate, String payloadHash) {
        try {
            String region = "eu-north-1";
            String service = "s3";
            String algorithm = "AWS4-HMAC-SHA256";

            String dateWithoutTime = xAmzDate.substring(0, 8);

            String canonicalUri = "/" + bucketName + "/" + key;
            String canonicalQueryString = "";
            String canonicalHeaders = "host:" + s3Endpoint.replace("https://", "") + "\n" + "x-amz-date:" + xAmzDate + "\n";
            String signedHeaders = "host;x-amz-date";

            String canonicalRequest = method + "\n" +
                    canonicalUri + "\n" +
                    canonicalQueryString + "\n" +
                    canonicalHeaders + "\n" +
                    signedHeaders + "\n" +
                    payloadHash;

            String credentialScope = dateWithoutTime + "/" + region + "/" + service + "/aws4_request";
            String stringToSign = algorithm + "\n" +
                    xAmzDate + "\n" +
                    credentialScope + "\n" +
                    hash(canonicalRequest);

            byte[] signingKey = getSignatureKey(secretKey, dateWithoutTime, region, service);
            String signature = toHex(hmacSHA256(signingKey, stringToSign));

            return algorithm + " " +
                    "Credential=" + accessKey + "/" + credentialScope + ", " +
                    "SignedHeaders=" + signedHeaders + ", " +
                    "Signature=" + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SigV4 authorization header: " + e.getMessage(), e);
        }
    }

    private byte[] getSignatureKey(String secretKey, String date, String region, String service) throws Exception {
        byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSHA256(kSecret, date);
        byte[] kRegion = hmacSHA256(kDate, region);
        byte[] kService = hmacSHA256(kRegion, service);
        return hmacSHA256(kService, "aws4_request");
    }

    private byte[] hmacSHA256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String hash(String data) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return toHex(hashedBytes);
    }

    private String toHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
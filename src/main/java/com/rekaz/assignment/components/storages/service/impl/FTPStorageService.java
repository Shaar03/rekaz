package com.rekaz.assignment.components.storages.service.impl;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;
import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.blobs.repository.BlobMetadataRepo;
import com.rekaz.assignment.components.storages.service.StorageService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

@Service("ftpStorageService")
public class FTPStorageService implements StorageService {

    @Value("${FTP_SERVER}")
    private String ftpServer;

    @Value("${FTP_PORT}")
    private int ftpPort;

    @Value("${FTP_USERNAME}")
    private String ftpUsername;

    @Value("${FTP_PASSWORD}")
    private String ftpPassword;

    @Value("${FTP_ROOT_DIR}")
    private String ftpRootDir;

    private final BlobMetadataRepo blobMetadataRepo;

    public FTPStorageService(BlobMetadataRepo blobMetadataRepo) {
        this.blobMetadataRepo = blobMetadataRepo;
    }

    private FTPClient connectToFtp() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpServer, ftpPort);
        ftpClient.login(ftpUsername, ftpPassword);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    @Override
    public void save(String id, String data) {
        FTPClient ftpClient = null;
        try {
            byte[] decodedData = Base64.getDecoder().decode(data);
            ftpClient = connectToFtp();

            String remoteFilePath = ftpRootDir + "/" + id;
            boolean uploaded = ftpClient.storeFile(remoteFilePath, new ByteArrayInputStream(decodedData));

            if (!uploaded) {
                throw new RuntimeException("Failed to upload file to FTP server.");
            }

            BlobMetadata metadata = new BlobMetadata();
            metadata.setId(id);
            metadata.setStorageType(StorageTypeEnum.FTP);
            metadata.setSize(decodedData.length);
            metadata.setCreatedAt(new Date());
            blobMetadataRepo.save(metadata);

        } catch (IOException e) {
            throw new RuntimeException("Error saving blob to FTP server: " + e.getMessage(), e);
        } finally {
            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public BlobResponse retrieve(String id) {
        FTPClient ftpClient = null;
        try {
            ftpClient = connectToFtp();

            String remoteFilePath = ftpRootDir + "/" + id;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean retrieved = ftpClient.retrieveFile(remoteFilePath, outputStream);

            if (!retrieved) {
                throw new RuntimeException("Failed to retrieve file from FTP server.");
            }

            byte[] fileData = outputStream.toByteArray();
            String encodedData = Base64.getEncoder().encodeToString(fileData);

            BlobMetadata metadata = blobMetadataRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blob metadata not found for id: " + id));

            return new BlobResponse(id, encodedData, metadata.getSize(), metadata.getCreatedAt());

        } catch (IOException e) {
            throw new RuntimeException("Error retrieving blob from FTP server: " + e.getMessage(), e);
        } finally {
            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
    }
}

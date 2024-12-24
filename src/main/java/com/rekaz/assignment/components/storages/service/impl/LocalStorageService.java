package com.rekaz.assignment.components.storages.service.impl;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;
import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.blobs.repository.BlobMetadataRepo;
import com.rekaz.assignment.components.storages.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.Date;

@Service("localStorageService")
public class LocalStorageService implements StorageService {

    @Value("${STORAGE_ROOT_PATH}")
    private String storageRootPath;

    private final BlobMetadataRepo blobMetadataRepo;

    public LocalStorageService(BlobMetadataRepo blobMetadataRepo) {
        this.blobMetadataRepo = blobMetadataRepo;
    }

    @Override
    public void save(String id, String data) {
        try {
            Path filePath = Paths.get(storageRootPath).resolve(id).toAbsolutePath();

            Path parentDir = filePath.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            byte[] decodedData = Base64.getDecoder().decode(data);

            Files.write(filePath, decodedData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            BlobMetadata metadata = new BlobMetadata();
            metadata.setId(id);
            metadata.setStorageType(StorageTypeEnum.LOCAL_FILE_SYSTEM);
            metadata.setSize(decodedData.length);
            metadata.setCreatedAt(new Date());
            blobMetadataRepo.save(metadata);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save blob to local storage: " + e.getMessage(), e);
        }
    }

    @Override
    public BlobResponse retrieve(String id) {
        try {
            Path filePath = Paths.get(storageRootPath).resolve(id).toAbsolutePath();

            if (!Files.exists(filePath)) {
                throw new RuntimeException("Blob file not found for ID: " + id);
            }

            BlobMetadata metadata = blobMetadataRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blob metadata not found for id: " + id));

            byte[] fileData = Files.readAllBytes(filePath);

            String encodedData = Base64.getEncoder().encodeToString(fileData);

            return new BlobResponse(id, encodedData, metadata.getSize(), metadata.getCreatedAt());
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve blob from local storage: " + e.getMessage(), e);
        }
    }
}

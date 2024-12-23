package com.rekaz.assignment.components.blobs.service.impl;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;
import com.rekaz.assignment.components.blobs.entity.BlobData;
import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.blobs.repository.BlobDataRepo;
import com.rekaz.assignment.components.blobs.repository.BlobMetadataRepo;
import com.rekaz.assignment.components.blobs.service.StorageService;
import org.springframework.stereotype.Service;

@Service("dbStorageService")
public class DatabaseStorageService implements StorageService {

    private final BlobDataRepo blobDataRepo;
    private final BlobMetadataRepo blobMetadataRepo;

    public DatabaseStorageService(BlobDataRepo blobDataRepo, BlobMetadataRepo blobMetadataRepo) {
        this.blobDataRepo = blobDataRepo;
        this.blobMetadataRepo = blobMetadataRepo;
    }

    @Override
    public void save(String id, String data) {
        byte[] decodedData = java.util.Base64.getDecoder().decode(data);

        BlobMetadata metadata = new BlobMetadata();
        metadata.setId(id);
        metadata.setStorageType(StorageTypeEnum.DB_TABLE);
        metadata.setSize(decodedData.length);
        blobMetadataRepo.saveAndFlush(metadata);

        BlobData blobData = new BlobData();
        blobData.setBase64Data(data);
        blobData.setMetadata(metadata);
        blobDataRepo.save(blobData);
    }

    @Override
    public BlobResponse retrieve(String id) {
        BlobData blobData = blobDataRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Blob not found"));

        BlobMetadata metadata = blobMetadataRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Blob metadata not found"));

        return new BlobResponse(
                metadata.getId(),
                blobData.getBase64Data(),
                metadata.getSize(),
                metadata.getCreatedAt()
        );
    }
}

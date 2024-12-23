package com.rekaz.assignment.components.blobs.service.impl;

import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.blobs.repository.BlobMetadataRepo;
import com.rekaz.assignment.components.blobs.service.BlobService;
import org.springframework.stereotype.Component;

@Component
public class BlobServiceImpl implements BlobService {

    private final BlobMetadataRepo blobMetadataRepo;

    public BlobServiceImpl(BlobMetadataRepo blobMetadataRepo) {
        this.blobMetadataRepo = blobMetadataRepo;
    }

    @Override
    public StorageTypeEnum getTypeFromId(String id) {
        return blobMetadataRepo.findStorageTypeById(id);
    }
}

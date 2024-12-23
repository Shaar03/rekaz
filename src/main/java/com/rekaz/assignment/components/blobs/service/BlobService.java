package com.rekaz.assignment.components.blobs.service;

import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import org.springframework.stereotype.Service;

@Service
public interface BlobService {
    StorageTypeEnum getTypeFromId(String id);
}

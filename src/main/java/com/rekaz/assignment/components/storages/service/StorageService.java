package com.rekaz.assignment.components.storages.service;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;
import org.springframework.stereotype.Service;

@Service
public interface StorageService {
    void save(String id, String data);
    BlobResponse retrieve(String id);
}

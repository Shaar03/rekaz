package com.rekaz.assignment.components.blobs.service;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;

public interface StorageService {
    void save(String id, String data);
    BlobResponse retrieve(String id);
}

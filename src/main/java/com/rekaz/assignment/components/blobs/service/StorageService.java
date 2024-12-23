package com.rekaz.assignment.components.blobs.service;

public interface StorageService {
    void save(String id, String data);
    String retrieve(String id);
}

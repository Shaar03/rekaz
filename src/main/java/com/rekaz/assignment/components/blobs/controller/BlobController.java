package com.rekaz.assignment.components.blobs.controller;

import com.rekaz.assignment.components.blobs.dto.BlobResponse;
import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.blobs.request.BlobStorageRequest;
import com.rekaz.assignment.components.blobs.service.BlobService;
import com.rekaz.assignment.components.storages.resolver.StorageServiceResolver;
import com.rekaz.assignment.components.storages.service.StorageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/blobs")
public class BlobController {

    private final StorageServiceResolver storageServiceResolver;

    private final BlobService blobService;

    public BlobController(StorageServiceResolver storageServiceResolver, BlobService blobService) {
        this.storageServiceResolver = storageServiceResolver;
        this.blobService = blobService;
    }

    @PostMapping
    public ResponseEntity<?> saveBlob(@Valid @RequestBody BlobStorageRequest request) {
        try {
            StorageService storageService = storageServiceResolver.resolve(request.getType());

            storageService.save(request.getId(), request.getData());

            return ResponseEntity.ok("Blob saved successfully using " + request.getType());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving blob: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlob(@PathVariable String id) {
        try {
            StorageTypeEnum storageType = blobService.getTypeFromId(id);

            if (storageType == null) {
                return ResponseEntity.status(404).body("Blob metadata not found for id: " + id);
            }

            StorageService storageService = storageServiceResolver.resolve(storageType);

            BlobResponse response = storageService.retrieve(id);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving blob: " + e.getMessage());
        }
    }
}

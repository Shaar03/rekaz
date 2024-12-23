package com.rekaz.assignment.components.blobs.repository;

import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlobMetadataRepo extends JpaRepository<BlobMetadata, String> {
    @Query("SELECT b.storageType FROM BlobMetadata b WHERE b.id = :id")
    StorageTypeEnum findStorageTypeById(String id);
}

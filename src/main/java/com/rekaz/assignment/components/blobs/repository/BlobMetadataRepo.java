package com.rekaz.assignment.components.blobs.repository;

import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlobMetadataRepo extends JpaRepository<BlobMetadata, String> {
}

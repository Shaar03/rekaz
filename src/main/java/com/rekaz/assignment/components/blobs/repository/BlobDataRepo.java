package com.rekaz.assignment.components.blobs.repository;

import com.rekaz.assignment.components.blobs.entity.BlobData;
import com.rekaz.assignment.components.blobs.entity.BlobMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlobDataRepo extends JpaRepository<BlobData, Long> {
    Optional<BlobData> findByMetadata(BlobMetadata metadata);
}

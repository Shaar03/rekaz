package com.rekaz.assignment.components.blobs.repository;

import com.rekaz.assignment.components.blobs.entity.BlobData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlobDataRepo extends JpaRepository<BlobData, String> {
}

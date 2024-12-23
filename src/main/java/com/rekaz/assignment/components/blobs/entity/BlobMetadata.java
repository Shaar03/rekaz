package com.rekaz.assignment.components.blobs.entity;

import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class BlobMetadata {

    @Id
    private String id;

    @Enumerated(EnumType.ORDINAL)
    private StorageTypeEnum storageType;

    private long size;

    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}

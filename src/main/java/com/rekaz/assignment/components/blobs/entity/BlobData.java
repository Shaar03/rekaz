package com.rekaz.assignment.components.blobs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class BlobData {

    @Id
    private String id;

    private String base64Data;

    @OneToOne
    @JoinColumn(name = "id")
    private BlobMetadata metadata;
}


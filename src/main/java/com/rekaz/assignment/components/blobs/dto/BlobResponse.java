package com.rekaz.assignment.components.blobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BlobResponse {
    private String id;
    private String data;
    private long size;
    private Date createdAt;
}

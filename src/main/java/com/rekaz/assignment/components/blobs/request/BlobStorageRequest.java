package com.rekaz.assignment.components.blobs.request;

import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

@Data
public class BlobStorageRequest {

    @NonNull
    @NotEmpty
    private String id;

    @NonNull
    @NotEmpty
    private String data;

    @NonNull
    @NotEmpty
    private StorageTypeEnum type;
}

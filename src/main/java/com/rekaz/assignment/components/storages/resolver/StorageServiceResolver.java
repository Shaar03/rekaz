package com.rekaz.assignment.components.storages.resolver;

import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.storages.service.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StorageServiceResolver {

    private final Map<StorageTypeEnum, StorageService> storageServices;

    public StorageServiceResolver(
            @Qualifier("dbStorageService") StorageService dbStorageService,
            @Qualifier("localStorageService") StorageService localStorageService,
            @Qualifier("s3StorageService") StorageService s3StorageService,
            @Qualifier("ftpStorageService") StorageService ftpStorageService
    ) {
        this.storageServices = Map.of(
                StorageTypeEnum.DB_TABLE, dbStorageService,
                StorageTypeEnum.LOCAL_FILE_SYSTEM, localStorageService,
                StorageTypeEnum.S3, s3StorageService,
                StorageTypeEnum.FTP, ftpStorageService
        );
    }

    public StorageService resolve(StorageTypeEnum type) {
        StorageService service = storageServices.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported storage type: " + type);
        }
        return service;
    }
}

package com.rekaz.assignment.components.storages.resolver;

import com.rekaz.assignment.components.blobs.enums.StorageTypeEnum;
import com.rekaz.assignment.components.storages.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StorageServiceResolver {

    private final Map<StorageTypeEnum, StorageService> storageServices;

    @Autowired
    public StorageServiceResolver(
            @Qualifier("dbStorageService") StorageService dbStorageService,
            @Qualifier("localStorageService") StorageService localStorageService
            // TODO: add other storage services if needed
    ) {
        this.storageServices = Map.of(
                StorageTypeEnum.DB_TABLE, dbStorageService,
                StorageTypeEnum.LOCAL_FILE_SYSTEM, localStorageService
                // TODO: add other types
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

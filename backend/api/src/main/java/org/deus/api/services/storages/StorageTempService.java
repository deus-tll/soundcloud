package org.deus.api.services.storages;

import me.desair.tus.server.TusFileUploadService;
import org.deus.api.enums.FileType;
import org.deus.api.storages.drivers.StorageDriverInterface;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.Map;

@Service
@AllArgsConstructor
public class StorageTempService {
    private final StorageDriverInterface storage;
    private final TusFileUploadService tusFileUploadService;
    private final String tempBucketName = "temp_files";

    public void putContent(String uploadURI, Map<String, String> metadata, FileType fileType) {

    }
}

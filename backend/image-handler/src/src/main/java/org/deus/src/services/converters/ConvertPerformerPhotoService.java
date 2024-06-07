package org.deus.src.services.converters;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.services.ConverterService;
import org.deus.src.services.storage.StoragePerformerPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConvertPerformerPhotoService {
    private final StoragePerformerPhotoService storagePerformerPhotoService;
    private final ConverterService converterService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertPerformerPhotoService.class);

    public void convertPerformerPhoto(long performerId, int targetWidth, int targetHeight) throws DataIsNotPresentException, DataProcessingException {
        Optional<byte[]> optionalOriginalBytes = storagePerformerPhotoService.getOriginalBytes(performerId);

        if (optionalOriginalBytes.isEmpty()) {
            String errorMessage = "OriginalBytes of requested performer photo was not present when trying to convert";
            logger.error(errorMessage);
            throw new DataIsNotPresentException(errorMessage);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] data = converterService.convert(targetWidth, targetHeight, "image/webp", outputStream, optionalOriginalBytes.get());

            storagePerformerPhotoService.putNewBytesAsFile(performerId, data);
        }
        catch (IOException e) {
            String errorMessage = "Error while converting performer photo";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
        catch (DataSavingException e) {
            String errorMessage = "Error while saving converted performer photo";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
    }
}

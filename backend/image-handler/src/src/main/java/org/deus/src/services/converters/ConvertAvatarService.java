package org.deus.src.services.converters;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.data.DataIsNotPresentException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.services.ConverterService;
import org.deus.src.services.storage.StorageAvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConvertAvatarService {
    private final StorageAvatarService storageAvatarService;
    private final ConverterService converterService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertAvatarService.class);

    public void convertAvatar(long userId, int targetWidth, int targetHeight) throws DataIsNotPresentException, DataProcessingException {
        Optional<byte[]> optionalOriginalBytes = storageAvatarService.getOriginalBytes(userId);

        if (optionalOriginalBytes.isEmpty()) {
            String errorMessage = "OriginalBytes of requested avatar was not present when trying to convert";
            logger.error(errorMessage);
            throw new DataIsNotPresentException(errorMessage);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] data = converterService.convert(targetWidth, targetHeight, "image/webp", outputStream, optionalOriginalBytes.get());

            storageAvatarService.putNewBytesAsFile(userId, data);
        }
        catch (IOException e) {
            String errorMessage = "Error while converting avatar";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
        catch (DataSavingException e) {
            String errorMessage = "Error while saving converted avatar";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
    }
}
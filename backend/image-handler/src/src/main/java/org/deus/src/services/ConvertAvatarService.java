package org.deus.src.services;

import lombok.AllArgsConstructor;
import org.deus.datalayerstarter.exceptions.data.DataIsNotPresentException;
import org.deus.datalayerstarter.exceptions.data.DataProcessingException;
import org.deus.datalayerstarter.exceptions.data.DataSavingException;
import org.deus.storagestarter.services.StorageAvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
@ComponentScan(basePackageClasses = {StorageAvatarService.class})
public class ConvertAvatarService {
    private final StorageAvatarService storageAvatarService;
    private static final Logger logger = LoggerFactory.getLogger(ConvertAvatarService.class);

    public void convertAvatar(long userId) throws DataIsNotPresentException, DataProcessingException {
        Optional<byte[]> optionalOriginalBytes = storageAvatarService.getOriginalBytes(userId);

        if (optionalOriginalBytes.isEmpty()) {
            String errorMessage = "OriginalBytes of requested avatar was not present when trying to convert";
            logger.error(errorMessage);
            throw new DataIsNotPresentException(errorMessage);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(optionalOriginalBytes.get()));

            if (image == null) {
                throw new IOException("Failed to read image");
            }

            boolean success = ImageIO.write(image, "webp", outputStream);

            if (!success) {
                throw new IOException("Failed to convert image to WebP format");
            }

            byte[] webpData = outputStream.toByteArray();

            storageAvatarService.putWebP(userId, webpData);
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

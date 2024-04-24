package org.deus.src.services;

import lombok.AllArgsConstructor;
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

    public void convertAvatar(long userId) {
        Optional<byte[]> optionalOriginalBytes = storageAvatarService.getOriginalBytes(userId);

        optionalOriginalBytes.ifPresentOrElse(originalBytes -> {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalBytes));

                if (image == null) {
                    throw new IOException("Failed to read image");
                }

                boolean success = ImageIO.write(image, "webp", outputStream);

                if (!success) {
                    throw new IOException("Failed to convert image to WebP format");
                }

                byte[] webpData = outputStream.toByteArray();

                storageAvatarService.putWebP(userId, webpData);
            } catch (IOException e) {
                logger.error("Error while converting avatar", e);
            }
        }, () -> {
            logger.error("OriginalBytes of avatar was not present when trying to convert");
        });
    }
}

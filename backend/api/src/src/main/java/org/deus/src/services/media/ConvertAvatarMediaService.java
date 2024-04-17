package org.deus.src.services.media;

import org.deus.storagestarter.services.StorageAvatarService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@ComponentScan(basePackageClasses = {StorageAvatarService.class})
public class ConvertAvatarMediaService {
    private final StorageAvatarService storageAvatarService;

    public void convertAvatar(long userId) throws IOException {
        byte[] originalBytes = storageAvatarService.getOriginalBytes(userId);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalBytes));

        if (image == null) {
            throw new IOException("Failed to read image");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        boolean success = ImageIO.write(image, "webp", outputStream);

        if (!success) {
            throw new IOException("Failed to convert image to WebP format");
        }

        byte[] webpData = outputStream.toByteArray();

        outputStream.close();

        storageAvatarService.putWebP(userId, webpData);
    }
}

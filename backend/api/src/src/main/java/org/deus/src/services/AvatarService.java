package org.deus.src.services;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.StatusException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.models.auth.UserModel;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.storage.StorageAvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final StorageAvatarService storageAvatarService;
    private final RabbitMQService rabbitMQService;
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public ResponseEntity<String> avatarUpload(MultipartFile avatar, UserModel user) throws StatusException {
        try {
            storageAvatarService.putOriginalBytes(user.getId(), avatar.getBytes());

            rabbitMQService.sendUserDTO("convert.avatar", user.mapUserToDTO((String) null));

            return new ResponseEntity<>("Process of updating avatar has started. Please wait...", HttpStatus.OK);
        }
        catch (IOException | DataSavingException | MessageSendingException e) {
            String message = "Failed to upload avatar file!";
            logger.error(message, e);
            throw new StatusException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getAvatarUrl(Long userId) {
        return storageAvatarService.getPathToAvatar(userId);
    }
}

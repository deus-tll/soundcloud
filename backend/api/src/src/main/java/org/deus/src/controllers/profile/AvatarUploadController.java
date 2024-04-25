package org.deus.src.controllers.profile;

import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.deus.src.SrcApplication;
import org.deus.src.exceptions.StatusException;
import org.deus.src.services.auth.UserService;

import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.deus.storagestarter.services.StorageAvatarService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/profile/avatar")
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = {StorageAvatarService.class, RabbitMQService.class})
public class AvatarUploadController {
    private final StorageAvatarService avatarService;
    private final UserService userService;
    private final RabbitMQService rabbitMQService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) throws StatusException {
        if (avatar.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }

        try {
            avatarService.putOriginalBytes(userService.getCurrentUser().getId(), avatar.getBytes());

            rabbitMQService.sendUserId("convert.avatar", userService.getCurrentUser().getId());

            ////////// перенести в мікросервіс
            this.rabbitMQService.sendWebsocketMessageDTO(
                    "websocket.message.sent",
                    "/topic/avatars-ready",
                    "Your avatars are ready!",
                    null);
            //////////

            return new ResponseEntity<>("File uploaded successfully: ", HttpStatus.OK);
        } catch (Exception e) {
            String message = "Failed to upload avatar file!";
            SrcApplication.logger.error(message, e);
            throw new StatusException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

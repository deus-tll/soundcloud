package org.deus.src.controllers.profile;

import org.deus.rabbitmqstarter.services.RabbitMQService;
import org.deus.src.SrcApplication;
import org.deus.src.exceptions.StatusException;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.media.ConvertAvatarMediaService;

import org.deus.storagestarter.services.StorageAvatarService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/profile/avatar")
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = {StorageAvatarService.class, RabbitMQService.class})
public class AvatarUploadController {
    private final StorageAvatarService avatarService;
    private final UserService userService;
    private final ConvertAvatarMediaService convertAvatarMediaService;
    private final RabbitMQService rabbitMQService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) throws StatusException {
        if (avatar.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }

        try {
            avatarService.putOriginal(userService.getCurrentUser().getId(), avatar.getBytes());

            executorService.submit(() -> {
                try {
                    convertAvatarMediaService.convertAvatar(userService.getCurrentUser().getId());

                    this.rabbitMQService.sendWebsocketMessage(
                            "websocket.message.sent",
                            "/topic/avatars-ready",
                            "Your avatars are ready!",
                            null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("The task is executed in a thread: " + Thread.currentThread().getName());
            });

            return new ResponseEntity<>("File uploaded successfully: ", HttpStatus.OK);
        } catch (Exception e) {
            String message = "Failed to upload avatar file!";
            SrcApplication.logger.error(message, e);
            throw new StatusException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

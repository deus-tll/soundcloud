package org.deus.src.controllers.profile;

import org.deus.src.exceptions.StatusException;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.media.ConvertAvatarMediaService;
import org.deus.src.services.storages.StorageAvatarService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/profile/avatar")
@RequiredArgsConstructor
public class AvatarUploadController {
    private final StorageAvatarService avatarService;
    private final UserService userService;
    private final ConvertAvatarMediaService convertAvatarMediaService;
    private final SimpMessagingTemplate messagingTemplate;
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

                    messagingTemplate.convertAndSendToUser(
                            userService.getCurrentUser().getUsername(),
                            "/topic/avatars-ready",
                            "Your avatars are ready!"
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("The task is executed in a thread: " + Thread.currentThread().getName());
            });

            return new ResponseEntity<>("File uploaded successfully: ", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new StatusException("Failed to upload file!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

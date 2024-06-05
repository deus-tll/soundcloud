package org.deus.src.controllers.profile;

import org.deus.src.exceptions.StatusException;
import org.deus.src.services.AvatarService;
import org.deus.src.services.auth.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class AvatarUploadController {
    private final UserService userService;
    private final AvatarService avatarService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) throws StatusException {
        if (avatar.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }

        return avatarService.avatarUpload(avatar, userService.getCurrentUser());
    }
}

package org.deus.src.controllers.auth;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.StatusException;
import org.deus.src.services.auth.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private-api/auth")
@RequiredArgsConstructor
public class PrivateAuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/validate-token")
    public ResponseEntity<UserDetails> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String token = authorizationHeader.substring(7);

        try {
            UserDetails userDetails = authenticationService.validateToken(token);
            return ResponseEntity.ok(userDetails);
        } catch (StatusException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}

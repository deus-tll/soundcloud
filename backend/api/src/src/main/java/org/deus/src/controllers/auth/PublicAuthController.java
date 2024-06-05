package org.deus.src.controllers.auth;

import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.models.auth.UserModel;
import org.deus.src.requests.auth.SignInRequest;
import org.deus.src.requests.auth.SignUpRequest;
import org.deus.src.responses.auth.JwtAuthenticationResponse;
import org.deus.src.services.AvatarService;
import org.deus.src.services.auth.AuthenticationService;
import org.deus.src.services.auth.UserService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class PublicAuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(summary = "User registration in the system")
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) throws StatusException {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "User login to the system")
    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) throws StatusException {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Getting the current user (by key)")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(){
        UserDTO userDTO = userService.getCurrentUserDTO();
        return ResponseEntity.ok(userDTO);
    }
}
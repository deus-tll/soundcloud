package org.deus.api.controllers.auth;

import org.deus.api.exceptions.StatusException;
import org.deus.api.services.auth.AuthenticationService;
import org.deus.api.services.auth.UserService;
import org.deus.api.dtos.auth.UserDTO;
import org.deus.api.requests.auth.SignInRequest;
import org.deus.api.requests.auth.SignUpRequest;
import org.deus.api.responses.auth.JwtAuthenticationResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(summary = "User registration in the system")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) throws StatusException {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "User login to the system")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) throws StatusException {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Getting the current user (by key)")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(){
        return ResponseEntity.ok(new UserDTO(userService.getCurrentUser()));
    }
}
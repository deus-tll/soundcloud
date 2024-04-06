package org.deus.src.controllers.auth;

import org.deus.src.dtos.auth.UserDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.auth.SignInRequest;
import org.deus.src.requests.auth.SignUpRequest;
import org.deus.src.responses.auth.JwtAuthenticationResponse;
import org.deus.src.services.auth.AuthenticationService;
import org.deus.src.services.auth.UserService;

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
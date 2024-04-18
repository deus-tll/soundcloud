package org.deus.src.services.auth;

import org.deus.dataobjectslayer.dtos.auth.UserDTO;
import org.deus.dataobjectslayer.models.auth.RoleEnum;
import org.deus.dataobjectslayer.models.auth.UserModel;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.auth.SignInRequest;
import org.deus.src.requests.auth.SignUpRequest;
import org.deus.src.responses.auth.JwtAuthenticationResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest request) throws StatusException {
        UserModel user = UserModel.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.ROLE_USER)
                .build();

        userService.create(user);

        String jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponse(jwt, new UserDTO(user));
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) throws StatusException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new StatusException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        UserModel user = userService.getByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponse(jwt, new UserDTO(user));
    }

    public UserDetails validateToken(String token) throws StatusException {
        String username = jwtService.extractUserName(token);
        if (username == null) {
            throw new StatusException("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.getByUsername(username);
        if (userDetails == null) {
            throw new StatusException("User not found", HttpStatus.UNAUTHORIZED);
        }

        return userDetails;
    }
}

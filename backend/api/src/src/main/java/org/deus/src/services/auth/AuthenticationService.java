package org.deus.src.services.auth;

import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.enums.RoleEnum;
import org.deus.src.exceptions.StatusException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.models.auth.UserModel;
import org.deus.src.requests.auth.SignInRequest;
import org.deus.src.requests.auth.SignUpRequest;
import org.deus.src.responses.auth.JwtAuthenticationResponse;

import org.deus.src.services.AvatarService;
import org.deus.src.services.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final AvatarService avatarService;
    private final JwtService jwtService;
    private final RabbitMQService rabbitMQService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public JwtAuthenticationResponse signUp(SignUpRequest request) throws StatusException {

        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            throw new StatusException("Confirmation password isn't matching", HttpStatus.BAD_REQUEST);
        }

        UserModel user = UserModel.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.USER)
                .build();

        userService.create(user);

        Long userId = user.getId();

        UserDTO userDTO = new UserDTO(userId, user.getUsername(), user.getEmail(), user.getRole(), avatarService.getAvatarUrl(userId));

        String jwt = jwtService.generateToken(user);

        try {
            rabbitMQService.sendUserDTO("user.register", userDTO);
        } catch (MessageSendingException e) {
            logger.error("Something went wrong while trying to inform other microservices about user's registration", e);
        }

        return new JwtAuthenticationResponse(jwt, userDTO);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) throws StatusException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new StatusException(e.getMessage() + ", try again or if you don't have account yet, create one.", HttpStatus.UNAUTHORIZED);
        }

        UserModel user = userService.getByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);

        Long userId = user.getId();

        UserDTO userDTO = new UserDTO(userId, user.getUsername(), user.getEmail(), user.getRole(), avatarService.getAvatarUrl(userId));

        return new JwtAuthenticationResponse(jwt, userDTO);
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

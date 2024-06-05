package org.deus.src.services.auth;

import org.deus.src.dtos.fromModels.UserDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.models.auth.UserModel;
import org.deus.src.repositories.UserRepository;

import org.deus.src.services.AvatarService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final AvatarService avatarService;

    public UserModel save(UserModel user) {
        return repository.save(user);
    }

    public UserModel create(UserModel user) throws StatusException {
        if (repository.existsByUsername(user.getUsername())) {
            throw new StatusException("A user with this name already exists", HttpStatus.CONFLICT);
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new StatusException("A user with this email already exists", HttpStatus.CONFLICT);
        }

        return this.save(user);
    }

    public UserModel getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public UserModel getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public UserDTO getCurrentUserDTO() {
        UserModel user = this.getCurrentUser();
        return user.mapUserToDTO(avatarService.getAvatarUrl(user.getId()));
    }
}

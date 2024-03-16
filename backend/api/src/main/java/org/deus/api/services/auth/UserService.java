package org.deus.api.services.auth;

import org.deus.api.models.auth.UserModel;
import org.deus.api.repositories.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserModel save(UserModel user) {
        return repository.save(user);
    }

    public UserModel create(UserModel user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("A user with this name already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("A user with this email already exists");
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
}

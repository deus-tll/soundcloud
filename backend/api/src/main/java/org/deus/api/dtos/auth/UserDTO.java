package org.deus.api.dtos.auth;

import org.deus.api.models.auth.RoleEnum;
import org.deus.api.models.auth.UserModel;

import lombok.Data;

@Data
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final RoleEnum role;

    public UserDTO(UserModel user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        role = user.getRole();
    }
}

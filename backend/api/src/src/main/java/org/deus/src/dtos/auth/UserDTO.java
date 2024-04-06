package org.deus.src.dtos.auth;

import org.deus.src.models.auth.RoleEnum;
import org.deus.src.models.auth.UserModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserDTO implements Serializable {
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

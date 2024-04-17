package org.deus.dataobjectslayer.dtos.auth;

import lombok.Data;
import org.deus.dataobjectslayer.models.auth.RoleEnum;
import org.deus.dataobjectslayer.models.auth.UserModel;

import java.io.Serializable;

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
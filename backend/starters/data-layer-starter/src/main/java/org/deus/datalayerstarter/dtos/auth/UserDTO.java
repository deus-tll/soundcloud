package org.deus.datalayerstarter.dtos.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.datalayerstarter.JsonSerializer;
import org.deus.datalayerstarter.models.auth.RoleEnum;
import org.deus.datalayerstarter.models.auth.UserModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends JsonSerializer {
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

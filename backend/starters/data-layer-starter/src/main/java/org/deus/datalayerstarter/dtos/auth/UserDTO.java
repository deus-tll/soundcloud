package org.deus.datalayerstarter.dtos.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.datalayerstarter.JsonSerializer;
import org.deus.datalayerstarter.enums.auth.RoleEnum;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends JsonSerializer {
    private final Long id;
    private final String username;
    private final String email;
    private final RoleEnum role;

    public UserDTO(Long id, String username, String email, RoleEnum role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}

package org.deus.datalayerstarter.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.datalayerstarter.JsonSerializer;
import org.deus.datalayerstarter.enums.auth.RoleEnum;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserDTO extends JsonSerializer {
    private final Long id;
    private final String username;
    private final String email;
    private final RoleEnum role;
}

package org.deus.src.dtos.fromModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.src.dtos.JsonSerializer;
import org.deus.src.enums.RoleEnum;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserDTO extends JsonSerializer {
    private final Long id;
    private final String username;
    private final String email;
    private final RoleEnum role;
}

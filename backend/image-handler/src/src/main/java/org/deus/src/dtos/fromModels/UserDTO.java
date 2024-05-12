package org.deus.src.dtos.fromModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.deus.src.enums.RoleEnum;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private RoleEnum role;
}

package org.deus.datalayerstarter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.datalayerstarter.dtos.auth.UserDTO;

import java.util.Set;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class PerformerDTO {
    private Long id;
    private String name;
    private UserDTO user;
    private Set<SongDTO> songs;
}

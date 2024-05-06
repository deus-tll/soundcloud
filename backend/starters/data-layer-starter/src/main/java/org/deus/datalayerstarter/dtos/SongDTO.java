package org.deus.datalayerstarter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deus.datalayerstarter.dtos.auth.UserDTO;

import java.util.Set;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class SongDTO {
    private Long id;
    private String name;
    private UserDTO uploader;
    private Set<PerformerDTO> performers;
}

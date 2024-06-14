package org.deus.src.dtos.fromModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.deus.src.enums.SongStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongDTO {
    private Long id;
    private String name;
    private UserDTO uploader;
    private Set<PerformerDTO> performers;
    private SongStatus status;
    private String url;
    private String coverUrl;
}
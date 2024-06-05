package org.deus.src.dtos.creatings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongCreatingDTO {
    private Long userId;
    private Long songId;
    private String fileId;
    private String uploaderUsername;
}

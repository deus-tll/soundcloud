package org.deus.src.dtos.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongConvertingDTO {
    private Long userId;
    private Long songId;
    private String fileId;
    private String uploaderUsername;
}

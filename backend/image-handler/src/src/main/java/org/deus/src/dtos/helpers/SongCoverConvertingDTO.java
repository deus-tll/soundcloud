package org.deus.src.dtos.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongCoverConvertingDTO {
    private Long songId;
    private String uploaderUsername;
}

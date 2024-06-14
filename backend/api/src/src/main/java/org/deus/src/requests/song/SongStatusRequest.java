package org.deus.src.requests.song;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.deus.src.enums.SongStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongStatusRequest {
    private SongStatus status;
}

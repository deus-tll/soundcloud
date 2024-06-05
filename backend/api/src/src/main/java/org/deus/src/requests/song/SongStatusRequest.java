package org.deus.src.requests.song;

import lombok.Data;
import org.deus.src.enums.SongStatus;

@Data
public class SongStatusRequest {
    private SongStatus status;
}

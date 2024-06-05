package org.deus.src.requests;

import lombok.Data;
import org.deus.src.enums.SongStatus;

@Data
public class SongStatusRequest {
    private SongStatus status;
}

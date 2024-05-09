package org.deus.src.requests.song;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongUpdateRequest {
    @Schema(description = "The title of the song", example = "The Show Must Go On")
    private String name;

    @Schema(description = "IDs of the performers")
    private Set<Long> performerIds;
}

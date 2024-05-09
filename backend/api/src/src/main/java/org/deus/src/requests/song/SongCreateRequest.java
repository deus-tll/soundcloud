package org.deus.src.requests.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongCreateRequest {
    @Schema(description = "The title of the song", example = "The Show Must Go On")
    @NotBlank(message = "The name cannot be empty")
    private String name;

    @Schema(description = "ID of the uploader user")
    @NotNull(message = "The uploader ID cannot be null")
    private Long uploaderId;

    @Schema(description = "IDs of the performers")
    private Set<Long> performerIds;

    @Schema(description = "")
    @NotBlank(message = "The fileId cannot be empty")
    private String fileId;
}
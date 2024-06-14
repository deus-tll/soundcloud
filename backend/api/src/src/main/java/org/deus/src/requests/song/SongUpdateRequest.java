package org.deus.src.requests.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongUpdateRequest {
    @Schema(description = "The title of the song", example = "The Show Must Go On")
    private String name;

    @Schema(description = "IDs of the performers")
    private Set<Long> performerIds;

    @Schema(description = "FileId in temporary storage")
    private String fileId;

    @Schema(description = "Song's photo cover")
    private MultipartFile cover;
}

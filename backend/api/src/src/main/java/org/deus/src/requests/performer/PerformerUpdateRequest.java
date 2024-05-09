package org.deus.src.requests.performer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformerUpdateRequest {
    @Schema(description = "Flag indicating whether the current user that made request is performer he updates")
    @NotBlank(message = "The isUser flag cannot be empty")
    private boolean isUser;

    @Schema(description = "Performer's name", example = "John Doe")
    @Size(min = 1, max = 50, message = "The name must be between 1 and 50 characters long")
    private String name;
}

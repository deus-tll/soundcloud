package org.deus.api.requests.auth;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "Request for login")
public class SignInRequest {
    @Schema(description = "Username", example = "john_doe")
    @NotBlank(message = "The username cannot be empty")
    private String username;

    @Schema(description = "Password", example = "my_1secret1_password")
    @NotBlank(message = "The password address cannot be empty")
    private String password;
}

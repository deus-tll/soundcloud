package org.deus.api.dtos.errors;

import lombok.Data;

@Data
public class ErrorResponseDTO {

    private final String status;
    private final String message;

    public ErrorResponseDTO(Exception ex) {
        this.status = ex.getClass().toString();
        this.message = ex.getMessage();
    }
}

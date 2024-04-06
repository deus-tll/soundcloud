package org.deus.src.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class StatusException extends Exception {
    private final HttpStatus status;

    public StatusException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

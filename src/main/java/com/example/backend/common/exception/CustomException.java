package com.example.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public CustomException(CustomExceptionEnum customExceptionEnum) {
        this.message = customExceptionEnum.getMessage();
        this.status = customExceptionEnum.getStatus();
    }
}


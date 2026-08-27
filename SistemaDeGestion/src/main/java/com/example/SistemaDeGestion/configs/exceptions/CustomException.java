package com.example.SistemaDeGestion.configs.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public abstract class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final List<String> errors;

    public CustomException(String message, HttpStatus status, List<String> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }
}
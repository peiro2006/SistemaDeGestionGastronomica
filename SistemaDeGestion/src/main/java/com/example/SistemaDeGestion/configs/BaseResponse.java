package com.example.SistemaDeGestion.configs;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BaseResponse<T> {

    private final T data;
    private final String message;
    private final List<String> errors;
    private final String timestamp;

    private BaseResponse(T data, String message, List<String> errors, String timestamp) {
        this.data = data;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static <T> BaseResponse<T> ok(T data, String message) {
        return new BaseResponse<>(data, message, null, getCurrentTimestamp());
    }

    private static String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    public static <T> BaseResponse<T> noContent(String message) {
        return new BaseResponse<>(null, message, null, getCurrentTimestamp());
    }

    public static <T> BaseResponse<T> error(String message, List<String> errors) {
        return new BaseResponse<>(null, message, errors, getCurrentTimestamp());
    }
}
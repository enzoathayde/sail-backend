package br.java.sail.dtos;

import org.springframework.http.HttpStatus;

public record StandardResponse<T>(
        String message,
        boolean error,
        T data
) {}

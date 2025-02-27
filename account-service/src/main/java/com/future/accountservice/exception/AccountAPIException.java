package com.future.accountservice.exception;

import org.springframework.http.HttpStatus;

import lombok.*;

@Getter
public class AccountAPIException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public AccountAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

package com.machpay.affiliate.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccountDeletedException extends RuntimeException {
    public AccountDeletedException(String message) {
        super(message);
    }

    public AccountDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
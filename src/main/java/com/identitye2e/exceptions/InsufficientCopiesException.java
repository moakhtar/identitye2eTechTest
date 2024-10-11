package com.identitye2e.exceptions;

public class InsufficientCopiesException extends RuntimeException {
    public InsufficientCopiesException(String message) {
        super(message);
    }
}

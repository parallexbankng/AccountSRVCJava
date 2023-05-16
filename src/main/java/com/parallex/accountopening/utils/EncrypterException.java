package com.parallex.accountopening.utils;

public class EncrypterException extends RuntimeException {
    public EncrypterException(String message, Exception e) {
        super(message, e);
    }
}


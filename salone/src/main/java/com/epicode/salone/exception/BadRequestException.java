package com.epicode.salone.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String messaggio) {
        super(messaggio);
    }
}
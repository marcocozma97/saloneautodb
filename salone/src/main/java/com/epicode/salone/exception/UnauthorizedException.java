package com.epicode.salone.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String messaggio) {
        super(messaggio);
    }
}
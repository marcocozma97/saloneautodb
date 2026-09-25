package com.epicode.salone.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String messaggio) {
        super(messaggio);
    }
}
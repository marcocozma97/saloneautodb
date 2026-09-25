package com.epicode.salone.exception;

import com.epicode.salone.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> gestisciNotFound(NotFoundException e) {
        return costruisci(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> gestisciBadRequest(BadRequestException e) {
        return costruisci(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> gestisciUnauthorized(UnauthorizedException e) {
        return costruisci(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> gestisciConflict(ConflictException e) {
        return costruisci(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> gestisciValidazione(MethodArgumentNotValidException e) {
        String messaggio = e.getBindingResult().getFieldErrors().stream()
                .map(errore -> errore.getField() + ": " + errore.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return costruisci(HttpStatus.BAD_REQUEST, messaggio);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> gestisciJsonNonValido(HttpMessageNotReadableException e) {
        return costruisci(HttpStatus.BAD_REQUEST, "Corpo della richiesta non valido");
    }

    private ResponseEntity<ErrorResponse> costruisci(HttpStatus stato, String messaggio) {
        return ResponseEntity.status(stato).body(new ErrorResponse(stato.value(), messaggio));
    }
}
package com.epicode.salone.controller;

import com.epicode.salone.dto.LoginRequest;
import com.epicode.salone.dto.LoginResponse;
import com.epicode.salone.dto.RegistrazioneRequest;
import com.epicode.salone.dto.UtenteResponse;
import com.epicode.salone.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrazione")
    @ResponseStatus(HttpStatus.CREATED)
    public UtenteResponse registrazione(@Valid @RequestBody RegistrazioneRequest richiesta) {
        return authService.registra(richiesta);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest richiesta) {
        return authService.login(richiesta);
    }
}
package com.epicode.salone.controller;

import com.epicode.salone.dto.ProfiloRequest;
import com.epicode.salone.dto.UtenteResponse;
import com.epicode.salone.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profilo")
public class ProfiloController {

    private final UtenteService utenteService;

    public ProfiloController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping
    public UtenteResponse getProfilo(Authentication authentication) {
        return utenteService.getProfilo(idUtente(authentication));
    }

    // l'utente da modificare è sempre quello del token
    @PutMapping
    public UtenteResponse aggiornaProfilo(Authentication authentication,
                                          @Valid @RequestBody ProfiloRequest richiesta) {
        return utenteService.aggiornaProfilo(idUtente(authentication), richiesta);
    }

    // "elimina il mio account": solo il proprio, preso dal token
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaAccount(Authentication authentication) {
        utenteService.eliminaAccount(idUtente(authentication));
    }

    private Long idUtente(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
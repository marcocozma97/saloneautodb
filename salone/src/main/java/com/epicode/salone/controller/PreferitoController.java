package com.epicode.salone.controller;

import com.epicode.salone.dto.PreferitoRequest;
import com.epicode.salone.dto.PreferitoResponse;
import com.epicode.salone.service.PreferitoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferiti")
public class PreferitoController {

    private final PreferitoService preferitoService;

    public PreferitoController(PreferitoService preferitoService) {
        this.preferitoService = preferitoService;
    }

    @GetMapping
    public List<PreferitoResponse> elenco(Authentication authentication) {
        return preferitoService.elenco(idUtente(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreferitoResponse aggiungi(Authentication authentication,
                                      @Valid @RequestBody PreferitoRequest richiesta) {
        return preferitoService.aggiungi(idUtente(authentication), richiesta.autoId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuovi(Authentication authentication, @PathVariable Long id) {
        preferitoService.rimuovi(idUtente(authentication), id);
    }

    // l'id dell'utente arriva SOLO dal token
    private Long idUtente(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
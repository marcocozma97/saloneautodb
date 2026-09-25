package com.epicode.salone.controller;

import com.epicode.salone.dto.UtenteResponse;
import com.epicode.salone.service.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profilo")
public class ProfiloController {

    private final UtenteService utenteService;

    public ProfiloController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping
    public UtenteResponse getProfilo(Authentication authentication) {
        Long idUtente = (Long) authentication.getPrincipal();
        return utenteService.getProfilo(idUtente);
    }
}
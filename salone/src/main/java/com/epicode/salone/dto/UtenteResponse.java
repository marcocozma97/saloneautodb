package com.epicode.salone.dto;

import com.epicode.salone.entity.Utente;

public record UtenteResponse(Long id, String nome, String email, String ruolo) {

    public static UtenteResponse from(Utente utente) {
        return new UtenteResponse(
                utente.getId(),
                utente.getNome(),
                utente.getEmail(),
                utente.getRuolo().name()
        );
    }
}
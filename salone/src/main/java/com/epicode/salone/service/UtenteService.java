package com.epicode.salone.service;

import com.epicode.salone.dto.UtenteResponse;
import com.epicode.salone.entity.Utente;
import com.epicode.salone.exception.NotFoundException;
import com.epicode.salone.repository.UtenteRepository;
import org.springframework.stereotype.Service;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public UtenteResponse getProfilo(Long idUtente) {
        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
        return UtenteResponse.from(utente);
    }
}
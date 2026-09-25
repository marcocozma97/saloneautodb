package com.epicode.salone.service;

import com.epicode.salone.dto.ProfiloRequest;
import com.epicode.salone.dto.UtenteResponse;
import com.epicode.salone.entity.Ruolo;
import com.epicode.salone.entity.Utente;
import com.epicode.salone.exception.BadRequestException;
import com.epicode.salone.exception.NotFoundException;
import com.epicode.salone.repository.AvvisoRepository;
import com.epicode.salone.repository.PreferitoRepository;
import com.epicode.salone.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PreferitoRepository preferitoRepository;
    private final AvvisoRepository avvisoRepository;

    public UtenteService(UtenteRepository utenteRepository,
                         PreferitoRepository preferitoRepository,
                         AvvisoRepository avvisoRepository) {
        this.utenteRepository = utenteRepository;
        this.preferitoRepository = preferitoRepository;
        this.avvisoRepository = avvisoRepository;
    }

    public UtenteResponse getProfilo(Long idUtente) {
        return UtenteResponse.from(trovaUtente(idUtente));
    }

    // il profilo riceve un DTO con il solo nome.
    public UtenteResponse aggiornaProfilo(Long idUtente, ProfiloRequest richiesta) {
        Utente utente = trovaUtente(idUtente);
        utente.setNome(richiesta.nome().trim());
        return UtenteResponse.from(utenteRepository.save(utente));
    }

    // "elimina il mio account" cancella avvisi e preferiti
    @Transactional
    public void eliminaAccount(Long idUtente) {
        Utente utente = trovaUtente(idUtente);

        // se l'admin eliminasse se stesso, il salone resterebbe senza amministratore
        if (utente.getRuolo() == Ruolo.ADMIN) {
            throw new BadRequestException("L'account amministratore non può essere eliminato");
        }

        // prima i dati collegati (hanno la chiave esterna verso l'utente), poi l'utente
        avvisoRepository.deleteByUtenteId(idUtente);
        preferitoRepository.deleteByUtenteId(idUtente);
        utenteRepository.delete(utente);
    }

    private Utente trovaUtente(Long idUtente) {
        return utenteRepository.findById(idUtente)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }
}
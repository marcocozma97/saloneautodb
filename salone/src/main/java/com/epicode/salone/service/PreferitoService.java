package com.epicode.salone.service;

import com.epicode.salone.dto.PreferitoResponse;
import com.epicode.salone.entity.Auto;
import com.epicode.salone.entity.Preferito;
import com.epicode.salone.entity.Utente;
import com.epicode.salone.exception.ConflictException;
import com.epicode.salone.exception.NotFoundException;
import com.epicode.salone.repository.AutoRepository;
import com.epicode.salone.repository.AvvisoRepository;
import com.epicode.salone.repository.PreferitoRepository;
import com.epicode.salone.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PreferitoService {

    private final PreferitoRepository preferitoRepository;
    private final AvvisoRepository avvisoRepository;
    private final AutoRepository autoRepository;
    private final UtenteRepository utenteRepository;

    public PreferitoService(PreferitoRepository preferitoRepository,
                            AvvisoRepository avvisoRepository,
                            AutoRepository autoRepository,
                            UtenteRepository utenteRepository) {
        this.preferitoRepository = preferitoRepository;
        this.avvisoRepository = avvisoRepository;
        this.autoRepository = autoRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<PreferitoResponse> elenco(Long idUtente) {
        return preferitoRepository.findByUtenteId(idUtente).stream()
                // se l'admin rimette un'auto in bozza, l'utente non deve più vederla
                .filter(preferito -> preferito.getAuto().isPubblicata())
                .map(PreferitoResponse::from)
                .toList();
    }

    public PreferitoResponse aggiungi(Long idUtente, Long autoId) {
        // si possono aggiungere solo auto pubblicate
        Auto auto = autoRepository.findByIdAndPubblicataTrue(autoId)
                .orElseThrow(() -> new NotFoundException("Auto non trovata"));

        if (preferitoRepository.existsByUtenteIdAndAutoId(idUtente, autoId)) {
            throw new ConflictException("Auto già presente nei preferiti");
        }

        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Preferito preferito = new Preferito();
        preferito.setUtente(utente);
        preferito.setAuto(auto);
        preferito.setDataAggiunta(LocalDateTime.now());

        return PreferitoResponse.from(preferitoRepository.save(preferito));
    }

    @Transactional
    public void rimuovi(Long idUtente, Long idPreferito) {
        // id e proprietario insieme: il preferito di un altro risulta "non trovato"
        Preferito preferito = preferitoRepository.findByIdAndUtenteId(idPreferito, idUtente)
                .orElseThrow(() -> new NotFoundException("Preferito non trovato"));

        // senza preferito non ha senso l'avviso
        avvisoRepository.deleteByUtenteIdAndAutoId(idUtente, preferito.getAuto().getId());
        preferitoRepository.delete(preferito);
    }
}
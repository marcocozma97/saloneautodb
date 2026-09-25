package com.epicode.salone.service;

import com.epicode.salone.dto.AvvisoResponse;
import com.epicode.salone.dto.NuovoAvvisoRequest;
import com.epicode.salone.entity.Auto;
import com.epicode.salone.entity.Avviso;
import com.epicode.salone.entity.Utente;
import com.epicode.salone.exception.BadRequestException;
import com.epicode.salone.exception.ConflictException;
import com.epicode.salone.exception.NotFoundException;
import com.epicode.salone.repository.AutoRepository;
import com.epicode.salone.repository.AvvisoRepository;
import com.epicode.salone.repository.PreferitoRepository;
import com.epicode.salone.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AvvisoService {

    private final AvvisoRepository avvisoRepository;
    private final PreferitoRepository preferitoRepository;
    private final AutoRepository autoRepository;
    private final UtenteRepository utenteRepository;

    public AvvisoService(AvvisoRepository avvisoRepository,
                         PreferitoRepository preferitoRepository,
                         AutoRepository autoRepository,
                         UtenteRepository utenteRepository) {
        this.avvisoRepository = avvisoRepository;
        this.preferitoRepository = preferitoRepository;
        this.autoRepository = autoRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<AvvisoResponse> elenco(Long idUtente) {
        return avvisoRepository.findByUtenteId(idUtente).stream()
                .map(AvvisoResponse::from)
                .toList();
    }

    public AvvisoResponse dettaglio(Long idUtente, Long idAvviso) {
        return AvvisoResponse.from(trovaAvvisoDellUtente(idUtente, idAvviso));
    }

    public AvvisoResponse crea(Long idUtente, NuovoAvvisoRequest richiesta) {
        Auto auto = autoRepository.findByIdAndPubblicataTrue(richiesta.autoId())
                .orElseThrow(() -> new NotFoundException("Auto non trovata"));

        if (!preferitoRepository.existsByUtenteIdAndAutoId(idUtente, auto.getId())) {
            throw new BadRequestException("Per impostare un avviso aggiungi prima l'auto ai preferiti");
        }

        if (avvisoRepository.findByUtenteIdAndAutoId(idUtente, auto.getId()).isPresent()) {
            throw new ConflictException("Hai già un avviso su questa auto");
        }

        controllaSoglia(richiesta.soglia(), auto.getPrezzo());

        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Avviso avviso = new Avviso();
        avviso.setUtente(utente);
        avviso.setAuto(auto);
        avviso.setSoglia(richiesta.soglia());
        avviso.setInviato(false); // lo decide il server, non il client
        // token casuale e non indovinabile per il link di disattivazione nella mail
        avviso.setTokenDisattivazione(UUID.randomUUID().toString());
        avviso.setDataCreazione(LocalDateTime.now());

        return AvvisoResponse.from(avvisoRepository.save(avviso));
    }

    public AvvisoResponse modificaSoglia(Long idUtente, Long idAvviso, Integer nuovaSoglia) {
        Avviso avviso = trovaAvvisoDellUtente(idUtente, idAvviso);

        // ogni avviso manda una sola mail
        if (avviso.isInviato()) {
            throw new BadRequestException("Questo avviso ha già inviato la sua mail: eliminalo e creane uno nuovo");
        }

        controllaSoglia(nuovaSoglia, avviso.getAuto().getPrezzo());

        avviso.setSoglia(nuovaSoglia);
        return AvvisoResponse.from(avvisoRepository.save(avviso));
    }

    public void elimina(Long idUtente, Long idAvviso) {
        Avviso avviso = trovaAvvisoDellUtente(idUtente, idAvviso);
        avvisoRepository.delete(avviso);
    }

    // ---------- METODI DI SUPPORTO ----------

    private Avviso trovaAvvisoDellUtente(Long idUtente, Long idAvviso) {
        return avvisoRepository.findByIdAndUtenteId(idAvviso, idUtente)
                .orElseThrow(() -> new NotFoundException("Avviso non trovato"));
    }

    // l'avviso scatta quando il prezzo "attraversa" la soglia
    private void controllaSoglia(Integer soglia, Integer prezzoAttuale) {
        if (soglia >= prezzoAttuale) {
            throw new BadRequestException("La soglia deve essere inferiore al prezzo attuale dell'auto");
        }
    }
}
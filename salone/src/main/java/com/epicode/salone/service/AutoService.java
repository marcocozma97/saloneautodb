package com.epicode.salone.service;

import com.epicode.salone.dto.*;
import com.epicode.salone.entity.Auto;
import com.epicode.salone.exception.BadRequestException;
import com.epicode.salone.exception.NotFoundException;
import com.epicode.salone.repository.AutoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AutoService {

    // A sinistra: il valore che il client può mandare. A destra: il campo vero dell'entità.
    private static final Map<String, String> CAMPI_ORDINABILI = Map.of(
            "prezzo", "prezzo",
            "anno", "anno",
            "km", "chilometri",
            "marca", "marca",
            "recenti", "dataCreazione"
    );

    private final AutoRepository autoRepository;

    public AutoService(AutoRepository autoRepository) {
        this.autoRepository = autoRepository;
    }

    // ---------- PARTE PUBBLICA ----------

    public List<AutoPubblicaResponse> catalogo(String testo, String ordina, String direzione) {
        Sort ordinamento = costruisciOrdinamento(ordina, direzione);

        List<Auto> auto;
        if (testo == null || testo.isBlank()) {
            auto = autoRepository.findByPubblicataTrue(ordinamento);
        } else {
            String testoPulito = testo.trim();
            if (testoPulito.length() > 50) {
                throw new BadRequestException("Testo di ricerca troppo lungo");
            }
            // il testo viene passato come parametro alla query
            auto = autoRepository.cercaPubblicate(testoPulito, ordinamento);
        }

        return auto.stream()
                .map(AutoPubblicaResponse::from)
                .toList();
    }

    public AutoPubblicaResponse dettaglioPubblico(Long id) {
        // una bozza per il pubblico "non esiste": stessa risposta di un id inesistente
        Auto auto = autoRepository.findByIdAndPubblicataTrue(id)
                .orElseThrow(() -> new NotFoundException("Auto non trovata"));
        return AutoPubblicaResponse.from(auto);
    }

    // ---------- PARTE ADMIN ----------

    public List<AutoAdminResponse> tutteAdmin() {
        return autoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataCreazione"))
                .stream()
                .map(AutoAdminResponse::from)
                .toList();
    }

    public AutoAdminResponse dettaglioAdmin(Long id) {
        return AutoAdminResponse.from(trovaAuto(id));
    }

    public AutoAdminResponse crea(NuovaAutoRequest richiesta) {
        Auto auto = new Auto();
        auto.setMarca(richiesta.marca().trim());
        auto.setModello(richiesta.modello().trim());
        auto.setAnno(richiesta.anno());
        auto.setChilometri(richiesta.chilometri());
        auto.setDescrizione(richiesta.descrizione());
        auto.setPrezzo(richiesta.prezzo());
        auto.setPrezzoAcquisto(richiesta.prezzoAcquisto());
        auto.setPubblicata(richiesta.pubblicata());
        auto.setDataCreazione(LocalDateTime.now());

        return AutoAdminResponse.from(autoRepository.save(auto));
    }

    public AutoAdminResponse modifica(Long id, ModificaAutoRequest richiesta) {
        Auto auto = trovaAuto(id);
        auto.setMarca(richiesta.marca().trim());
        auto.setModello(richiesta.modello().trim());
        auto.setAnno(richiesta.anno());
        auto.setChilometri(richiesta.chilometri());
        auto.setDescrizione(richiesta.descrizione());
        auto.setPrezzoAcquisto(richiesta.prezzoAcquisto());
        auto.setPubblicata(richiesta.pubblicata());

        return AutoAdminResponse.from(autoRepository.save(auto));
    }

    // L'unico punto dove cambia il prezzo
    @Transactional
    public AutoAdminResponse cambiaPrezzo(Long id, Integer nuovoPrezzo) {
        Auto auto = trovaAuto(id);
        auto.setPrezzo(nuovoPrezzo);
        return AutoAdminResponse.from(autoRepository.save(auto));
    }

    // ---------- METODI DI SUPPORTO ----------

    private Auto trovaAuto(Long id) {
        return autoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Auto non trovata"));
    }

    private Sort costruisciOrdinamento(String ordina, String direzione) {
        String campo = CAMPI_ORDINABILI.get(ordina);
        if (campo == null) {
            throw new BadRequestException("Ordinamento non ammesso. Valori validi: prezzo, anno, km, marca, recenti");
        }

        Sort.Direction verso;
        if ("asc".equals(direzione)) {
            verso = Sort.Direction.ASC;
        } else if ("desc".equals(direzione)) {
            verso = Sort.Direction.DESC;
        } else {
            throw new BadRequestException("Direzione non ammessa. Valori validi: asc, desc");
        }

        return Sort.by(verso, campo);
    }
}
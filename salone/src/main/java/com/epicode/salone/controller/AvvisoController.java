package com.epicode.salone.controller;

import com.epicode.salone.dto.AvvisoResponse;
import com.epicode.salone.dto.DisattivaRequest;
import com.epicode.salone.dto.NuovoAvvisoRequest;
import com.epicode.salone.dto.SogliaRequest;
import com.epicode.salone.service.AvvisoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avvisi")
public class AvvisoController {

    private final AvvisoService avvisoService;

    public AvvisoController(AvvisoService avvisoService) {
        this.avvisoService = avvisoService;
    }

    @GetMapping
    public List<AvvisoResponse> elenco(Authentication authentication) {
        return avvisoService.elenco(idUtente(authentication));
    }

    @GetMapping("/{id}")
    public AvvisoResponse dettaglio(Authentication authentication, @PathVariable Long id) {
        return avvisoService.dettaglio(idUtente(authentication), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvvisoResponse crea(Authentication authentication,
                               @Valid @RequestBody NuovoAvvisoRequest richiesta) {
        return avvisoService.crea(idUtente(authentication), richiesta);
    }

    @PutMapping("/{id}")
    public AvvisoResponse modificaSoglia(Authentication authentication,
                                         @PathVariable Long id,
                                         @Valid @RequestBody SogliaRequest richiesta) {
        return avvisoService.modificaSoglia(idUtente(authentication), id, richiesta.soglia());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimina(Authentication authentication, @PathVariable Long id) {
        avvisoService.elimina(idUtente(authentication), id);
    }

    // raggiungibile senza login: l'utente arriva dal link nella mail
    @PostMapping("/disattiva")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disattiva(@Valid @RequestBody DisattivaRequest richiesta) {
        avvisoService.disattivaConToken(richiesta.token());
    }

    private Long idUtente(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
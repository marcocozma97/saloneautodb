package com.epicode.salone.controller;

import com.epicode.salone.dto.AutoPubblicaResponse;
import com.epicode.salone.service.AutoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auto")
public class AutoController {

    private final AutoService autoService;

    public AutoController(AutoService autoService) {
        this.autoService = autoService;
    }

    @GetMapping
    public List<AutoPubblicaResponse> catalogo(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "recenti") String ordina,
            @RequestParam(defaultValue = "desc") String direzione) {
        return autoService.catalogo(q, ordina, direzione);
    }

    @GetMapping("/{id}")
    public AutoPubblicaResponse dettaglio(@PathVariable Long id) {
        return autoService.dettaglioPubblico(id);
    }
}
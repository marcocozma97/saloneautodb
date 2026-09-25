package com.epicode.salone.controller;

import com.epicode.salone.dto.AutoAdminResponse;
import com.epicode.salone.dto.ModificaAutoRequest;
import com.epicode.salone.dto.NuovaAutoRequest;
import com.epicode.salone.dto.PrezzoRequest;
import com.epicode.salone.service.AutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/auto")
public class AdminAutoController {

    private final AutoService autoService;

    public AdminAutoController(AutoService autoService) {
        this.autoService = autoService;
    }

    @GetMapping
    public List<AutoAdminResponse> tutte() {
        return autoService.tutteAdmin();
    }

    @GetMapping("/{id}")
    public AutoAdminResponse dettaglio(@PathVariable Long id) {
        return autoService.dettaglioAdmin(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutoAdminResponse crea(@Valid @RequestBody NuovaAutoRequest richiesta) {
        return autoService.crea(richiesta);
    }

    @PutMapping("/{id}")
    public AutoAdminResponse modifica(@PathVariable Long id,
                                      @Valid @RequestBody ModificaAutoRequest richiesta) {
        return autoService.modifica(id, richiesta);
    }

    @PatchMapping("/{id}/prezzo")
    public AutoAdminResponse cambiaPrezzo(@PathVariable Long id,
                                          @Valid @RequestBody PrezzoRequest richiesta) {
        return autoService.cambiaPrezzo(id, richiesta.prezzo());
    }
}
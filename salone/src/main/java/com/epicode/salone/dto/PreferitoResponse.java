package com.epicode.salone.dto;

import com.epicode.salone.entity.Preferito;

import java.time.LocalDateTime;

public record PreferitoResponse(
        Long id,
        AutoPubblicaResponse auto,
        LocalDateTime dataAggiunta
) {
    public static PreferitoResponse from(Preferito preferito) {
        return new PreferitoResponse(
                preferito.getId(),
                AutoPubblicaResponse.from(preferito.getAuto()),
                preferito.getDataAggiunta()
        );
    }
}
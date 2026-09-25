package com.epicode.salone.dto;

import com.epicode.salone.entity.Avviso;

import java.time.LocalDateTime;

public record AvvisoResponse(
        Long id,
        Long autoId,
        String marca,
        String modello,
        Integer prezzoAttuale,
        Integer soglia,
        boolean inviato,
        LocalDateTime dataCreazione
) {
    public static AvvisoResponse from(Avviso avviso) {
        return new AvvisoResponse(
                avviso.getId(),
                avviso.getAuto().getId(),
                avviso.getAuto().getMarca(),
                avviso.getAuto().getModello(),
                avviso.getAuto().getPrezzo(),
                avviso.getSoglia(),
                avviso.isInviato(),
                avviso.getDataCreazione()
        );
    }
}
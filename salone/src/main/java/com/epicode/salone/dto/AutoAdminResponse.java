package com.epicode.salone.dto;

import com.epicode.salone.entity.Auto;

import java.time.LocalDateTime;

public record AutoAdminResponse(
        Long id,
        String marca,
        String modello,
        Integer anno,
        Integer chilometri,
        String descrizione,
        Integer prezzo,
        Integer prezzoAcquisto,
        boolean pubblicata,
        LocalDateTime dataCreazione
) {
    public static AutoAdminResponse from(Auto auto) {
        return new AutoAdminResponse(
                auto.getId(),
                auto.getMarca(),
                auto.getModello(),
                auto.getAnno(),
                auto.getChilometri(),
                auto.getDescrizione(),
                auto.getPrezzo(),
                auto.getPrezzoAcquisto(),
                auto.isPubblicata(),
                auto.getDataCreazione()
        );
    }
}
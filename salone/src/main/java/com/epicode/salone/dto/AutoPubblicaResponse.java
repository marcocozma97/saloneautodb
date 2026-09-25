package com.epicode.salone.dto;

import com.epicode.salone.entity.Auto;

public record AutoPubblicaResponse(
        Long id,
        String marca,
        String modello,
        Integer anno,
        Integer chilometri,
        String descrizione,
        Integer prezzo
) {
    public static AutoPubblicaResponse from(Auto auto) {
        return new AutoPubblicaResponse(
                auto.getId(),
                auto.getMarca(),
                auto.getModello(),
                auto.getAnno(),
                auto.getChilometri(),
                auto.getDescrizione(),
                auto.getPrezzo()
        );
    }
}
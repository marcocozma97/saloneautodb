package com.epicode.salone.dto;

import jakarta.validation.constraints.*;

public record ModificaAutoRequest(

        @NotBlank(message = "la marca è obbligatoria")
        @Size(max = 50, message = "massimo 50 caratteri")
        String marca,

        @NotBlank(message = "il modello è obbligatorio")
        @Size(max = 50, message = "massimo 50 caratteri")
        String modello,

        @NotNull(message = "l'anno è obbligatorio")
        @Min(value = 1900, message = "anno non valido")
        @Max(value = 2100, message = "anno non valido")
        Integer anno,

        @NotNull(message = "i chilometri sono obbligatori")
        @Min(value = 0, message = "i chilometri non possono essere negativi")
        Integer chilometri,

        @Size(max = 2000, message = "massimo 2000 caratteri")
        String descrizione,

        @NotNull(message = "il prezzo d'acquisto è obbligatorio")
        @Positive(message = "il prezzo d'acquisto deve essere maggiore di zero")
        @Max(value = 10000000, message = "prezzo troppo alto")
        Integer prezzoAcquisto,

        @NotNull(message = "indicare se l'auto è pubblicata")
        Boolean pubblicata
) {
}
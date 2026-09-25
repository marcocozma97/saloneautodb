package com.epicode.salone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrezzoRequest(

        @NotNull(message = "il prezzo è obbligatorio")
        @Positive(message = "il prezzo deve essere maggiore di zero")
        @Max(value = 10000000, message = "prezzo troppo alto")
        Integer prezzo
) {
}
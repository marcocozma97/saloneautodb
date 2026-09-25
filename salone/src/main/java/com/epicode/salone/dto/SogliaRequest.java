package com.epicode.salone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SogliaRequest(

        @NotNull(message = "la soglia è obbligatoria")
        @Positive(message = "la soglia deve essere maggiore di zero")
        @Max(value = 10000000, message = "soglia troppo alta")
        Integer soglia
) {
}
package com.epicode.salone.dto;

import jakarta.validation.constraints.NotNull;

public record PreferitoRequest(

        @NotNull(message = "l'id dell'auto è obbligatorio")
        Long autoId
) {
}
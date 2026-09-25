package com.epicode.salone.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "l'email è obbligatoria")
        String email,

        @NotBlank(message = "la password è obbligatoria")
        String password
) {
}
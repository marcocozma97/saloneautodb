package com.epicode.salone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DisattivaRequest(

        @NotBlank(message = "il token è obbligatorio")
        @Size(max = 64, message = "token non valido")
        String token
) {
}
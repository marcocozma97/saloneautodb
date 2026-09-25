package com.epicode.salone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfiloRequest(

        @NotBlank(message = "il nome è obbligatorio")
        @Size(max = 50, message = "massimo 50 caratteri")
        String nome
) {
}
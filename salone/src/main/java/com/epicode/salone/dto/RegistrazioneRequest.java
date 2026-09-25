package com.epicode.salone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrazioneRequest(

        @NotBlank(message = "il nome è obbligatorio")
        @Size(max = 50, message = "massimo 50 caratteri")
        String nome,

        @NotBlank(message = "l'email è obbligatoria")
        @Email(message = "formato email non valido")
        @Size(max = 100, message = "massimo 100 caratteri")
        String email,

        @NotBlank(message = "la password è obbligatoria")
        @Size(min = 8, max = 72, message = "la password deve avere tra 8 e 72 caratteri")
        String password
) {
}
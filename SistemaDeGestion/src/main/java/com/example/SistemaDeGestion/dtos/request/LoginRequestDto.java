package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto (

        @NotBlank(message = "Debe ingresar su email")
        @Email(message = "Debe ingresar un email valido")
        String email,

        @NotBlank(message = "Debe ingresar su contrasena")
        String password

) {
}

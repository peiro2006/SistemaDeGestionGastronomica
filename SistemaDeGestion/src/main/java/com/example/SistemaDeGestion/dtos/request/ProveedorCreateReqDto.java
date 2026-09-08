package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProveedorCreateReqDto (

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "Máximo 150 caracteres")
        String nombre,

        @Size(max = 30)
        String telefono,

        @Size(max = 150)
        @Email
        String email,

        @Size(max = 255)
        String direccion,

        @Size(max = 100)
        String ciudad

) {
}
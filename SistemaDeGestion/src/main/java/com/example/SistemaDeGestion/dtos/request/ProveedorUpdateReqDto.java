package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ProveedorUpdateReqDto (

        @Size(max = 150)
        String nombre,

        @Size(max = 30)
        String telefono,

        @Size(max = 150)
        @Email
        String email,

        @Size(max = 255)
        String direccion,

        @Size(max = 100)
        String ciudad,

        Boolean activo

) {
}
package com.example.SistemaDeGestion.dtos.response;

import java.time.Instant;

public record ProveedorResDto (

        Long idProveedor,
        String nombre,
        String telefono,
        String email,
        String direccion,
        String ciudad,
        boolean activo,
        Instant fechaCreacion

) {
}
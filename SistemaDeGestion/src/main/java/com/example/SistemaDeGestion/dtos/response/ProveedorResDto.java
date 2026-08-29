package com.example.SistemaDeGestion.dtos.response;

import java.time.LocalDateTime;

public record ProveedorResDto (

        Long idProveedor,
        String razonSocial,
        String cuitRut,
        String telefono,
        String correo,
        String direccion,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaUltimaModificacion,
        String usuarioAlta,
        String usuarioUltimaModificacion

) {
}
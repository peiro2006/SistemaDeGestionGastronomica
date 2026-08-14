package com.example.SistemaDeGestion.dtos.response;

import java.time.Instant;

public record UsuarioCreateResDto (

        Long idUsuario,
        String nombre,
        String apellido,
        String email,
        String rol,
        Instant fechaCreacion

) {
}

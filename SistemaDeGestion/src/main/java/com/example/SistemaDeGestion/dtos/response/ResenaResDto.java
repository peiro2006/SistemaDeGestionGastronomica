package com.example.SistemaDeGestion.dtos.response;

import java.time.Instant;

public record ResenaResDto(
        Long idResena,
        Long idPedido,
        Long idUsuario,
        String nombreUsuario,
        String nombreProducto,
        Integer calificacion,
        String comentario,
        Instant fechaCreacion
) {
}

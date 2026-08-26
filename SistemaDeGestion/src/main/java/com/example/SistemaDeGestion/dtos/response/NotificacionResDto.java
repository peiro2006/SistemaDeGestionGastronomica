package com.example.SistemaDeGestion.dtos.response;

import java.time.Instant;

public record NotificacionResDto(
        Long idNotificacion,
        Long idProducto,
        String nombreProducto,
        String mensaje,
        Boolean leida,
        Instant fecha
) {
}

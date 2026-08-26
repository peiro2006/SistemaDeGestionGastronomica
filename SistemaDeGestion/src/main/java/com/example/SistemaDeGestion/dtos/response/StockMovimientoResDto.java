package com.example.SistemaDeGestion.dtos.response;

import java.time.Instant;

public record StockMovimientoResDto(

        Long idStockMovimiento,
        Long idProducto,
        String nombreProducto,
        Long idInsumo,
        String nombreInsumo,
        String tipo,
        Integer cantidad,
        String motivo,
        Integer saldoPosterior,
        Long idUsuario,
        String usuarioEmail,
        Instant fecha

) {
}

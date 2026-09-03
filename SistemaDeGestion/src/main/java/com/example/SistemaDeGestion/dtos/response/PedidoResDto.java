package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResDto (

        Long idPedido,
        Long idUsuario,
        Long idCaja,
        String estado,
        String metDePago,
        BigDecimal total,
        Instant fechaCreacion,
        Instant fechaActualizacion,
        List<PedidoItemResDto> items

) {
}
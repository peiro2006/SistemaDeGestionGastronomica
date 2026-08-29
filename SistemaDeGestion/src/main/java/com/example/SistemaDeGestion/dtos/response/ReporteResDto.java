package com.example.SistemaDeGestion.dtos.response;

import com.example.SistemaDeGestion.models.EstadoPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record ReporteResDto(
        Instant desde,
        Instant hasta,
        BigDecimal totalFacturado,
        BigDecimal promedioValorPorPedido,
        long cantidadPedidosCompletados,
        long cantidadPedidosPeriodo,
        Map<EstadoPedido, Long> pedidosPorEstado
) {}
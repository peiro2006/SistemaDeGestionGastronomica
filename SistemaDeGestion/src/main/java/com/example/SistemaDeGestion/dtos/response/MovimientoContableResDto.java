package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoContableResDto(
        Long idMovimiento,
        String tipo,
        BigDecimal monto,
        String concepto,
        String metodoPago,
        Long idCaja,
        Instant fecha,
        String registradoPor
) {}
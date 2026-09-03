package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;

public record CajaResumenDto (
        BigDecimal totalEfectivo,
        BigDecimal totalDebito,
        BigDecimal totalCredito,
        BigDecimal totalTransferencia,
        BigDecimal totalNoEfectivo,
        BigDecimal montoActual
) {
}

package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CajaArqueoResDto(
        Long idArqueo,
        Long idCaja,
        String nombreCaja,
        BigDecimal montoInicial,
        BigDecimal montoFinal,
        BigDecimal totalEfectivo,
        BigDecimal totalDebito,
        BigDecimal totalCredito,
        BigDecimal totalTransferencia,
        Integer cantidadPedidos,
        Instant fechaApertura,
        Instant fechaCierre,
        List<PedidoResDto> movimientos
) {
}

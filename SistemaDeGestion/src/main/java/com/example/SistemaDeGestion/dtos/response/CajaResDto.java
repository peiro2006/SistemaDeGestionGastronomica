package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CajaResDto (

        Long idCaja,
        String nombreCaja,
        BigDecimal montoInicial,
        String moneda,
        String descripcionCaja,
        Boolean activa,
        LocalDateTime fechaCreacion

) {
}
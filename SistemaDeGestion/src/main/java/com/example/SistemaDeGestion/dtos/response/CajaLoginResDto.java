package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;

public record CajaLoginResDto (

        Long idCaja,
        String nombre,
        BigDecimal montoActual,
        String token

) {
}
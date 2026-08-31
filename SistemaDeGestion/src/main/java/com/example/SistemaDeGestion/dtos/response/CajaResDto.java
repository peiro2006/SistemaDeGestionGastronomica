package com.example.SistemaDeGestion.dtos.response;

import com.example.SistemaDeGestion.models.EstadoCaja;

import java.math.BigDecimal;
import java.time.Instant;

public record CajaResDto (

        Long idCaja,
        String nombre,
        String descripcion,
        BigDecimal montoInicial,
        BigDecimal montoActual,
        EstadoCaja estado,
        Instant fechaCreacion,
        Instant fechaActualizacion,
        Long abiertaPor,
        Instant fechaApertura

) {
}
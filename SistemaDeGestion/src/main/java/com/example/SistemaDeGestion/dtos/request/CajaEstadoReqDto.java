package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CajaEstadoReqDto (

        @NotNull(message = "El estado es obligatorio")
        String estado,

        BigDecimal montoInicial

) {
}

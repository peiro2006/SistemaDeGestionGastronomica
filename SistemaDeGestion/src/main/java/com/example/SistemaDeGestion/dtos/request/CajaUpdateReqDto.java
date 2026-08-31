package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CajaUpdateReqDto (

        @Size(max = 100)
        String nombre,

        @Size(max = 255)
        String descripcion,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal montoInicial,

        @Size(min = 4, max = 20)
        String password

) {
}
package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConfiguracionUpdateReqDto(

        @NotNull(message = "El valor es obligatorio")
        BigDecimal valor

) {
}

package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;

public record ConfiguracionResDto(
        Long id,
        String clave,
        BigDecimal valor
) {
}

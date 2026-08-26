package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;

public record RecetaIngredienteResDto(

        Long idRecetaInsumo,
        Long idInsumo,
        String nombreInsumo,
        String unidadMedida,
        BigDecimal cantidad

) {
}

package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecetaIngredienteReqDto(

        Long idInsumo,
        String nombreInsumo,
        String unidadMedida,

        @NotNull(message = "Debe ingresar la cantidad requerida")
        @DecimalMin(value = "0.01", message = "La cantidad requerida debe ser mayor a 0")
        BigDecimal cantidad

) {
}

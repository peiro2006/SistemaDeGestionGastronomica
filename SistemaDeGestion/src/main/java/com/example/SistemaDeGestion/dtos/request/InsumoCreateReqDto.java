package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InsumoCreateReqDto(

        @NotBlank(message = "Debe ingresar un nombre para el insumo")
        String nombreInsumo,

        @NotBlank(message = "Debe ingresar la unidad de medida")
        String unidadMedida,

        @NotNull(message = "Debe ingresar el stock inicial")
        @PositiveOrZero(message = "El stock inicial no puede ser negativo")
        Integer stockActual

) {
}

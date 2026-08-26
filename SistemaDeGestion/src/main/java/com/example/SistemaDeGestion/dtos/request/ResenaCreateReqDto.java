package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResenaCreateReqDto(
        @NotNull(message = "Debe ingresar la calificacion")
        @Min(value = 1, message = "La calificacion minima es 1")
        @Max(value = 5, message = "La calificacion maxima es 5")
        Integer calificacion,
        String comentario
) {
}

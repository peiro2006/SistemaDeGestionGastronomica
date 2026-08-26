package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockAjusteReqDto(

        Long idProducto,
        Long idInsumo,

        @NotBlank(message = "Debe ingresar el tipo de movimiento")
        String tipo,

        @NotNull(message = "Debe ingresar la cantidad")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotBlank(message = "Debe ingresar un motivo")
        String motivo

) {
}

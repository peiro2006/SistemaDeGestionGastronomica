package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoItemCreateReqDto(

        @NotNull(message = "Debe seleccionar un producto")
        Long idProducto,

        @NotNull(message = "Debe ingresar la cantidad")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad

) {
}

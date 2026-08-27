package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoItemReqDto (

        @NotNull(message = "Debe seleccionar un producto")
        Long idProducto,

        @NotNull(message = "Debe ingresar una cantidad")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad

) {
}
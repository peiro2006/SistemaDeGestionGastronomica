package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;

public record EstadoPedidoReqDto (

        @NotNull(message = "Debe seleccionar un estado")
        String estado

) {
}
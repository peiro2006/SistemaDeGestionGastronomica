package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record PedidoEstadoReqDto(
        @NotBlank(message = "Debe indicar el estado del pedido")
        String estado
) {
}

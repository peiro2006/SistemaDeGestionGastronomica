package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PedidoCreateReqDto (

        @NotNull(message = "Debe ingresar al menos un producto")
        @Size(min = 1, message = "Debe ingresar al menos un producto")
        List<PedidoItemReqDto> items

) {
}
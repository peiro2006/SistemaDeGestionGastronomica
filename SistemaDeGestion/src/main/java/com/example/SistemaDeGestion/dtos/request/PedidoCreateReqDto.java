package com.example.SistemaDeGestion.dtos.request;

import com.example.SistemaDeGestion.models.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PedidoCreateReqDto (

        @NotNull(message = "Debe ingresar al menos un producto")
        @Size(min = 1, message = "Debe ingresar al menos un producto")
        List<PedidoItemReqDto> items,

        @NotNull(message = "Debe seleccionar un metodo de pago")
        MetodoPago metDePago

) {
}
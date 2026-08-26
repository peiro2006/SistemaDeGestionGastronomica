package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoCreateReqDto(

        @NotEmpty(message = "Debe ingresar al menos un producto")
        @Valid
        List<PedidoItemCreateReqDto> items

) {
}

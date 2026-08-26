package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;

public record ProductoEstadoReqDto(

        @NotNull(message = "Debe indicar el estado del producto")
        Boolean activo

) {
}

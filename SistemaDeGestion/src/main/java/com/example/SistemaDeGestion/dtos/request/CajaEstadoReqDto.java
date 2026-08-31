package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotNull;

public record CajaEstadoReqDto (

        @NotNull(message = "El estado es obligatorio")
        String estado

) {
}
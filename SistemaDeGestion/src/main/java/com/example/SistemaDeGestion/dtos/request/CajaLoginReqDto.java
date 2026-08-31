package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CajaLoginReqDto (

        @NotNull(message = "El ID de la caja es obligatorio")
        Long idCaja,

        @NotBlank(message = "La contraseña es obligatoria")
        String password

) {
}
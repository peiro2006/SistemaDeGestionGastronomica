package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CajaCreateReqDto (

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "Máximo 100 caracteres")
        String nombre,

        @Size(max = 255)
        String descripcion,

        @Size(max = 3, min = 3, message = "La moneda debe tener 3 caracteres (ej: ARS)")
        String moneda,

        @NotNull(message = "El monto inicial es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
        BigDecimal montoInicial,

        @Size(min = 4, max = 20, message = "La contraseña debe tener entre 4 y 20 caracteres")
        String password

) {
}
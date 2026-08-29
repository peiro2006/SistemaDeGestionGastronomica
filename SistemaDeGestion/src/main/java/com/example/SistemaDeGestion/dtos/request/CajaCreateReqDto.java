package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CajaCreateReqDto (

        @NotBlank(message = "Debe ingresar un nombre o identificador para la caja")
        @Size(min = 2, max = 100, message = "El nombre de la caja debe tener entre 2 a 100 caracteres")
        String nombreCaja,

        @NotNull(message = "Debe ingresar el monto inicial de la caja")
        @DecimalMin(value = "0.0", message = "El monto inicial no puede ser negativo")
        BigDecimal montoInicial,

        @NotBlank(message = "Debe ingresar la moneda de la caja")
        @Pattern(regexp = "^[A-Z]{3}$", message = "La moneda debe ser un codigo ISO de 3 letras")
        String moneda,

        @Size(max = 500, message = "La descripcion de la caja no puede superar los 500 caracteres")
        String descripcionCaja,

        Boolean activa

) {
}
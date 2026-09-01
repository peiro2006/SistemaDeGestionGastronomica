package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MovimientoContableCreateReqDto(

        @NotBlank(message = "Debe indicar el tipo de movimiento")
        @Size(min = 1, max = 10, message = "Tipo de movimiento invalido")
        String tipo,

        @NotNull(message = "Debe indicar el monto del movimiento")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @Size(max = 255, message = "El concepto no puede superar los 255 caracteres")
        String concepto,

        @Size(max = 15, message = "Metodo de pago invalido")
        String metodoPago,

        Long idCaja

) {}
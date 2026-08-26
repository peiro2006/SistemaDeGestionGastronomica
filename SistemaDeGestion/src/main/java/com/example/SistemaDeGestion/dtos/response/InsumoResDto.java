package com.example.SistemaDeGestion.dtos.response;

public record InsumoResDto(

        Long idInsumo,
        String nombreInsumo,
        String unidadMedida,
        Integer stockActual

) {
}

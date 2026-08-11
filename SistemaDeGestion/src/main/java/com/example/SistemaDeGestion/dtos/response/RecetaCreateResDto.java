package com.example.SistemaDeGestion.dtos.response;

public record RecetaCreateResDto (

        Long idReceta,
        String nombreReceta,
        String descripcionReceta,
        String ingredientesReceta

) {
}

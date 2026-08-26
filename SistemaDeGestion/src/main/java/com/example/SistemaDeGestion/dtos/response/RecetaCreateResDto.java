package com.example.SistemaDeGestion.dtos.response;

import java.util.List;

public record RecetaCreateResDto (

        Long idReceta,
        String nombreReceta,
        String descripcionReceta,
        String ingredientesReceta,
        List<RecetaIngredienteResDto> ingredientes

) {
}

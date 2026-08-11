package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecetaCreateReqDto (

        @NotBlank(message = "Debe ingresar un nombre para la receta")
        @Size(min = 12, max = 24, message = "El nombre de la receta debe tener entre 12 a 24 caracteres")
        String nombreReceta,

        @NotBlank(message = "Debe ingresar una descripcion para la receta")
        String descripcionReceta,

        @NotBlank(message = "Debe ingresar los ingredientes de la receta")
        String ingredientesReceta

) {
}

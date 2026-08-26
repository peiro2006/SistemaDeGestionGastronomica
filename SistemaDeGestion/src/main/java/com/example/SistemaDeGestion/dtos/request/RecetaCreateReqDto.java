package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.util.List;

public record RecetaCreateReqDto (

        @NotBlank(message = "Debe ingresar un nombre para la receta")
        @Size(min = 2, max = 100, message = "El nombre de la receta debe tener entre 2 a 100 caracteres")
        String nombreReceta,

        @NotBlank(message = "Debe ingresar una descripcion para la receta")
        String descripcionReceta,

        String ingredientesReceta,

        @Valid
        List<RecetaIngredienteReqDto> ingredientes

) {
}

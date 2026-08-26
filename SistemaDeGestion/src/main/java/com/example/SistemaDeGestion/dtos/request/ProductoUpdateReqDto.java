package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoUpdateReqDto(

        @NotBlank(message = "Debe ingresar un nombre para el producto")
        @Size(min = 2, max = 100, message = "El producto debe tener entre 2 a 100 caracteres")
        String nombreProducto,

        @NotBlank(message = "Debe ingresar una descripcion para el producto")
        String descripcion,

        @NotBlank(message = "Debe ingresar un precio para el producto")
        @Pattern(regexp = "^(?!0+(\\.0+)?$)\\d+(\\.\\d{1,2})?$", message = "El precio debe ser numerico y mayor a 0")
        String precio,

        @NotBlank(message = "Debe ingresar una categoria para el producto")
        String categoria,

        String imagenUrl,

        @NotNull(message = "Debe ingresar el stock minimo del producto")
        @PositiveOrZero(message = "El stock minimo no puede ser negativo")
        Integer stockMinimo,

        @NotNull(message = "Debe seleccionar una receta para el producto")
        Long idReceta

) {
}

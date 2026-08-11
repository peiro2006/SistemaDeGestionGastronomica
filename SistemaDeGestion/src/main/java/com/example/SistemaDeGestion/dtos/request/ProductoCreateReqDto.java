package com.example.SistemaDeGestion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoCreateReqDto (

        @NotBlank(message = "Debe ingresar un nombre para el producto")
        @Size(min = 12, max = 24, message = "El producto debe tener entre 12 a 24 caracteres")
        String nombreProducto,

        @NotBlank(message = "Debe ingresar una descripcion para el producto")
        String descripcion,

        @NotBlank(message = "Debe ingresar un precio para el producto")
        String precio,

        @NotNull(message = "Debe seleccionar una receta para el producto")
        Long idReceta

) {
}

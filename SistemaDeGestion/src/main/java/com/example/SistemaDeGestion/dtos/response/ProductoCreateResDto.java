package com.example.SistemaDeGestion.dtos.response;

public record ProductoCreateResDto (

        Long idProducto,
        String nombreProducto,
        String descripcion,
        String precio,
        Long idReceta

) {
}

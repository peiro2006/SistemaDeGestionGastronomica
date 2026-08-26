package com.example.SistemaDeGestion.dtos.response;

public record ProductoCreateResDto (

        Long idProducto,
        String nombreProducto,
        String descripcion,
        String precio,
        String categoria,
        String imagenUrl,
        Boolean activo,
        Integer stockActual,
        Integer stockMinimo,
        Long idReceta,
        String nombreReceta

) {
}

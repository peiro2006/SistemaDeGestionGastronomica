package com.example.SistemaDeGestion.repositories.specs;

import com.example.SistemaDeGestion.models.Producto;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecs {

    private ProductoSpecs() {
    }

    public static Specification<Producto> byNombre(String nombre) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombreProducto")),
                        "%" + nombre.toLowerCase() + "%"
                );
    }

}

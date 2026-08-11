package com.example.SistemaDeGestion.repositories.specs;

import com.example.SistemaDeGestion.models.Receta;
import org.springframework.data.jpa.domain.Specification;

public class RecetaSpecs {

    private RecetaSpecs() {
    }

    public static Specification<Receta> byNombre(String nombre) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombreReceta")),
                        "%" + nombre.toLowerCase() + "%"
                );
    }

}

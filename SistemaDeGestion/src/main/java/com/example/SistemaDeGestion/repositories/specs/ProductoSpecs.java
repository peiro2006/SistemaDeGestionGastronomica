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

    public static Specification<Producto> byCategoria(String categoria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("categoria")),
                        categoria.toLowerCase()
                );
    }

    public static Specification<Producto> activos() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("activo"));
    }

    public static Specification<Producto> conStockDisponible() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("stockActual"), 0);
    }

}

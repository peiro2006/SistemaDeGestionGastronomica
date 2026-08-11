package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductosRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    boolean existsByNombreProductoIgnoreCase(String nombreProducto);

}

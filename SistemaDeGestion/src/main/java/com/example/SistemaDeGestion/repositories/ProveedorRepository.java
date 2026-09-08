package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findAllByOrderByNombre();

    List<Proveedor> findByActivoTrueOrderByNombre();

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
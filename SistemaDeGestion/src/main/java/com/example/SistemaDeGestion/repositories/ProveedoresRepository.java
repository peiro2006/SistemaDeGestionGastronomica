package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedoresRepository extends JpaRepository<Proveedor, Long> {

    boolean existsByCuitRut(String cuitRut);

    boolean existsByCuitRutAndIdProveedorNot(String cuitRut, Long idProveedor);

    List<Proveedor> findAllByOrderByFechaCreacionDesc();

}
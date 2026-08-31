package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Long> {

    List<Caja> findByEstadoOrderByFechaCreacionDesc(EstadoCaja estado);

    @Query("SELECT c FROM Caja c WHERE c.estado IN (:estados) ORDER BY c.fechaCreacion DESC")
    List<Caja> findByEstadoIn(@Param("estados") List<EstadoCaja> estados);

    Optional<Caja> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Caja> findAllByOrderByFechaCreacionDesc();
}
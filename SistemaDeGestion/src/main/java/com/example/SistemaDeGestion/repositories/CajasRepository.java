package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CajasRepository extends JpaRepository<Caja, Long> {

    boolean existsByNombreCajaIgnoreCase(String nombreCaja);

    List<Caja> findAllByOrderByFechaCreacionDesc();

}
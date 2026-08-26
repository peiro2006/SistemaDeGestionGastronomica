package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Insumo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsumosRepository extends JpaRepository<Insumo, Long> {

    boolean existsByNombreInsumoIgnoreCase(String nombreInsumo);

    Optional<Insumo> findByNombreInsumoIgnoreCase(String nombreInsumo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Insumo i where i.idInsumo = :id")
    Optional<Insumo> findByIdForUpdate(@Param("id") Long id);

}

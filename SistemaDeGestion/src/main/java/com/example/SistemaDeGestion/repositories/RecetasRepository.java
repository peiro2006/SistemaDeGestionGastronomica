package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecetasRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {

    boolean existsByNombreRecetaIgnoreCase(String nombreReceta);

    @Query("SELECT r FROM Receta r LEFT JOIN FETCH r.ingredientes ri LEFT JOIN FETCH ri.insumo WHERE r.idReceta = :id")
    Optional<Receta> findByIdConIngredientes(@Param("id") Long id);

}

package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetasRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {

    boolean existsByNombreRecetaIgnoreCase(String nombreReceta);

}

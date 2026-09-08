package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.CajaArqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CajaArqueoRepository extends JpaRepository<CajaArqueo, Long> {

    List<CajaArqueo> findByCajaOrderByFechaCierreDesc(Caja caja);

    List<CajaArqueo> findAllByOrderByFechaCierreDesc();
}

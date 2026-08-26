package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.StockMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovimientosRepository extends JpaRepository<StockMovimiento, Long> {

    List<StockMovimiento> findByProductoIdProductoOrderByFechaDesc(Long idProducto);

    List<StockMovimiento> findByInsumoIdInsumoOrderByFechaDesc(Long idInsumo);

    List<StockMovimiento> findAllByOrderByFechaDesc();

}

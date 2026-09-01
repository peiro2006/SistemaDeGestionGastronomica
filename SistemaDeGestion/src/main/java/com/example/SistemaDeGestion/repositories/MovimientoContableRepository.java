package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.MovimientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;

@Repository
public interface MovimientoContableRepository
        extends JpaRepository<MovimientoContable, Long>, JpaSpecificationExecutor<MovimientoContable> {

    @Query("SELECT SUM(m.monto) FROM MovimientoContable m " +
            "WHERE m.tipo = :tipo AND m.fecha >= :desde AND m.fecha <= :hasta")
    BigDecimal sumarMontoEntre(@Param("tipo") String tipo,
                               @Param("desde") Instant desde,
                               @Param("hasta") Instant hasta);
}

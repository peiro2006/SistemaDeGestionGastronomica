package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.MetodoPago;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    List<Pedido> findByEstadoOrderByFechaCreacionAsc(EstadoPedido estado);

    List<Pedido> findByUsuarioAndEstadoOrderByFechaCreacionDesc(Usuario usuario, EstadoPedido estado);

    List<Pedido> findByFechaCreacionBetweenOrderByFechaCreacionDesc(Instant desde, Instant hasta);

    List<Pedido> findByCajaOrderByFechaCreacionDesc(Caja caja);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.caja = :caja AND p.metDePago = :metodo AND p.estado != 'cancelado'")
    BigDecimal sumTotalByCajaAndMetDePago(@Param("caja") Caja caja, @Param("metodo") MetodoPago metodo);
}
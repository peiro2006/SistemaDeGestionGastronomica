package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    List<Pedido> findByEstadoOrderByFechaCreacionAsc(EstadoPedido estado);

    List<Pedido> findByUsuarioAndEstadoOrderByFechaCreacionDesc(Usuario usuario, EstadoPedido estado);

    List<Pedido> findByFechaCreacionBetweenOrderByFechaCreacionDesc(Instant desde, Instant hasta);
}
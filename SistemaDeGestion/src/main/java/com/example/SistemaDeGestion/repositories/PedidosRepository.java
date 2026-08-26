package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

}

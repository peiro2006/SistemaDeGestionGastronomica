package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenasRepository extends JpaRepository<Resena, Long> {

    Optional<Resena> findByPedidoIdPedido(Long idPedido);

    boolean existsByPedidoIdPedido(Long idPedido);

    List<Resena> findByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

}

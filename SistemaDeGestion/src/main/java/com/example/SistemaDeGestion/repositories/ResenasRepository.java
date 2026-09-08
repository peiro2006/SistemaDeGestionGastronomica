package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenasRepository extends JpaRepository<Resena, Long> {

    Optional<Resena> findByPedidoIdPedido(Long idPedido);

    boolean existsByPedidoIdPedido(Long idPedido);

    List<Resena> findByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    List<Resena> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT r FROM Resena r JOIN r.pedido p JOIN p.items pi JOIN pi.producto prod " +
           "WHERE LOWER(prod.nombreProducto) LIKE LOWER(CONCAT('%', :producto, '%')) " +
           "ORDER BY r.fechaCreacion DESC")
    List<Resena> findByProductoNombre(@Param("producto") String producto);

    @Query("SELECT r FROM Resena r JOIN r.usuario u " +
           "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "ORDER BY r.fechaCreacion DESC")
    List<Resena> findByUsuarioTexto(@Param("texto") String texto);

}

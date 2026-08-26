package com.example.SistemaDeGestion.repositories;

import com.example.SistemaDeGestion.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByLeidaFalseOrderByFechaDesc();

    List<Notificacion> findAllByOrderByFechaDesc();

    boolean existsByProductoIdProductoAndLeidaFalse(Long idProducto);

}

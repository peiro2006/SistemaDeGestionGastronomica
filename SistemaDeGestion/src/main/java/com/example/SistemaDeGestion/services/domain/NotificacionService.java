package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.NotificacionResDto;
import com.example.SistemaDeGestion.mappers.NotificacionMapper;
import com.example.SistemaDeGestion.models.Notificacion;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.repositories.NotificacionesRepository;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificacionService {

    private final NotificacionesRepository notificacionesRepository;
    private final ProductosRepository productosRepository;

    @Transactional(readOnly = true)
    public List<NotificacionResDto> listar(Boolean soloNoLeidas) {
        if (Boolean.TRUE.equals(soloNoLeidas)) {
            return NotificacionMapper.toResponseDtoList(notificacionesRepository.findAllByLeidaFalseOrderByFechaDesc());
        }
        return NotificacionMapper.toResponseDtoList(notificacionesRepository.findAllByOrderByFechaDesc());
    }

    @Transactional
    public NotificacionResDto marcarLeida(Long idNotificacion) {
        Notificacion notificacion = notificacionesRepository.findById(idNotificacion)
                .orElseThrow(() -> new NotFoundException("No existe la notificacion con id " + idNotificacion));
        notificacion.setLeida(true);
        return NotificacionMapper.toResponseDto(notificacionesRepository.save(notificacion));
    }

    @Transactional
    public void verificarStockBajo(Long idProducto) {
        Producto producto = productosRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            return;
        }

        int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();
        int stockMinimo = producto.getStockMinimo() == null ? 0 : producto.getStockMinimo();

        if (stockMinimo > 0 && stockActual <= stockMinimo) {
            boolean yaExisteAlerta = notificacionesRepository.existsByProductoIdProductoAndLeidaFalse(idProducto);
            if (!yaExisteAlerta) {
                Notificacion notificacion = new Notificacion();
                notificacion.setProducto(producto);
                notificacion.setMensaje(String.format(
                        "El producto '%s' tiene stock bajo. Actual: %d, minimo: %d",
                        producto.getNombreProducto(), stockActual, stockMinimo
                ));
                notificacion.setLeida(false);
                notificacionesRepository.save(notificacion);
            }
        }
    }

}

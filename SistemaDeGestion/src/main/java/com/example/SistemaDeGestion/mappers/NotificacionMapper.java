package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.NotificacionResDto;
import com.example.SistemaDeGestion.models.Notificacion;

import java.util.List;

public class NotificacionMapper {

    private NotificacionMapper() {
    }

    public static NotificacionResDto toResponseDto(Notificacion notificacion) {
        return new NotificacionResDto(
                notificacion.getIdNotificacion(),
                notificacion.getProducto() != null ? notificacion.getProducto().getIdProducto() : null,
                notificacion.getProducto() != null ? notificacion.getProducto().getNombreProducto() : null,
                notificacion.getMensaje(),
                notificacion.getLeida(),
                notificacion.getFecha()
        );
    }

    public static List<NotificacionResDto> toResponseDtoList(List<Notificacion> notificaciones) {
        return notificaciones.stream()
                .map(NotificacionMapper::toResponseDto)
                .toList();
    }

}

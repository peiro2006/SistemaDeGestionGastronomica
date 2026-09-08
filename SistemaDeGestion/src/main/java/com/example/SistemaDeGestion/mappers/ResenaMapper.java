package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.ResenaResDto;
import com.example.SistemaDeGestion.models.PedidoItem;
import com.example.SistemaDeGestion.models.Resena;

import java.util.List;

public class ResenaMapper {

    private ResenaMapper() {
    }

    public static ResenaResDto toResponseDto(Resena resena) {
        String nombreUsuario = resena.getUsuario().getNombre() + " " + resena.getUsuario().getApellido();
        String nombreProducto = null;
        if (resena.getPedido().getItems() != null && !resena.getPedido().getItems().isEmpty()) {
            PedidoItem primerItem = resena.getPedido().getItems().get(0);
            nombreProducto = primerItem.getProducto().getNombreProducto();
            if (resena.getPedido().getItems().size() > 1) {
                nombreProducto += " y +" + (resena.getPedido().getItems().size() - 1) + " mas";
            }
        }

        return new ResenaResDto(
                resena.getIdResena(),
                resena.getPedido().getIdPedido(),
                resena.getUsuario().getIdUsuario(),
                nombreUsuario.trim(),
                nombreProducto,
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFechaCreacion()
        );
    }

    public static List<ResenaResDto> toResponseDtoList(List<Resena> resenas) {
        return resenas.stream()
                .map(ResenaMapper::toResponseDto)
                .toList();
    }

}

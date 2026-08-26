package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.ResenaResDto;
import com.example.SistemaDeGestion.models.Resena;

import java.util.List;

public class ResenaMapper {

    private ResenaMapper() {
    }

    public static ResenaResDto toResponseDto(Resena resena) {
        return new ResenaResDto(
                resena.getIdResena(),
                resena.getPedido().getIdPedido(),
                resena.getUsuario().getIdUsuario(),
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

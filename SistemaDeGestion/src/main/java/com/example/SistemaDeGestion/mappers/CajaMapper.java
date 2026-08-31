package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.models.Caja;

import java.util.List;

public class CajaMapper {

    public static CajaResDto toResponseDto(Caja caja) {
        if (caja == null) return null;
        return new CajaResDto(
                caja.getIdCaja(),
                caja.getNombre(),
                caja.getDescripcion(),
                caja.getMontoInicial(),
                caja.getMontoActual(),
                caja.getEstado(),
                caja.getFechaCreacion(),
                caja.getFechaActualizacion(),
                caja.getAbiertaPor(),
                caja.getFechaApertura()
        );
    }

    public static List<CajaResDto> toResponseDtoList(List<Caja> cajas) {
        if (cajas == null) return List.of();
        return cajas.stream().map(CajaMapper::toResponseDto).toList();
    }
}
package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.MovimientoContableResDto;
import com.example.SistemaDeGestion.models.MovimientoContable;

import java.util.List;

public class MovimientoContableMapper {

    public static MovimientoContableResDto toResponseDto(MovimientoContable movimiento) {
        if (movimiento == null) {
            return null;
        }
        return new MovimientoContableResDto(
                movimiento.getIdMovimiento(),
                movimiento.getTipo(),
                movimiento.getMonto(),
                movimiento.getConcepto(),
                movimiento.getMetodoPago(),
                movimiento.getIdCaja(),
                movimiento.getFecha(),
                movimiento.getRegistradoPor()
        );
    }

    public static List<MovimientoContableResDto> toResponseDtoList(List<MovimientoContable> movimientos) {
        if (movimientos == null) {
            return List.of();
        }
        return movimientos.stream().map(MovimientoContableMapper::toResponseDto).toList();
    }
}

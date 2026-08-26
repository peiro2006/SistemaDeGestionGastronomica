package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.StockMovimientoResDto;
import com.example.SistemaDeGestion.models.StockMovimiento;

import java.util.List;

public class StockMovimientoMapper {

    private StockMovimientoMapper() {
    }

    public static StockMovimientoResDto toResponseDto(StockMovimiento movimiento) {
        return new StockMovimientoResDto(
                movimiento.getIdStockMovimiento(),
                movimiento.getProducto() != null ? movimiento.getProducto().getIdProducto() : null,
                movimiento.getProducto() != null ? movimiento.getProducto().getNombreProducto() : null,
                movimiento.getInsumo() != null ? movimiento.getInsumo().getIdInsumo() : null,
                movimiento.getInsumo() != null ? movimiento.getInsumo().getNombreInsumo() : null,
                movimiento.getTipo(),
                movimiento.getCantidad(),
                movimiento.getMotivo(),
                movimiento.getSaldoPosterior(),
                movimiento.getUsuario() != null ? movimiento.getUsuario().getIdUsuario() : null,
                movimiento.getUsuario() != null ? movimiento.getUsuario().getEmail() : null,
                movimiento.getFecha()
        );
    }

    public static List<StockMovimientoResDto> toResponseDtoList(List<StockMovimiento> movimientos) {
        return movimientos.stream()
                .map(StockMovimientoMapper::toResponseDto)
                .toList();
    }

}

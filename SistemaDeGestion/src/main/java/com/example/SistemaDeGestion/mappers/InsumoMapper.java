package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.InsumoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.InsumoResDto;
import com.example.SistemaDeGestion.models.Insumo;

import java.util.List;

public class InsumoMapper {

    private InsumoMapper() {
    }

    public static Insumo toModel(InsumoCreateReqDto request) {
        Insumo insumo = new Insumo();
        insumo.setNombreInsumo(request.nombreInsumo());
        insumo.setUnidadMedida(request.unidadMedida());
        insumo.setStockActual(request.stockActual());
        return insumo;
    }

    public static InsumoResDto toResponseDto(Insumo insumo) {
        return new InsumoResDto(
                insumo.getIdInsumo(),
                insumo.getNombreInsumo(),
                insumo.getUnidadMedida(),
                insumo.getStockActual()
        );
    }

    public static List<InsumoResDto> toResponseDtoList(List<Insumo> insumos) {
        return insumos.stream()
                .map(InsumoMapper::toResponseDto)
                .toList();
    }

}
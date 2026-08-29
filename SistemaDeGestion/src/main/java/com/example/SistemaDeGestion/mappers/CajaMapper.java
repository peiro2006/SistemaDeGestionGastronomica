package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.models.Caja;

import java.util.List;

public class CajaMapper {

    private CajaMapper() {
    }

    public static Caja toModel(CajaCreateReqDto request) {
        Caja caja = new Caja();
        caja.setNombreCaja(request.nombreCaja());
        caja.setMontoInicial(request.montoInicial());
        caja.setMoneda(request.moneda().toUpperCase());
        caja.setDescripcionCaja(request.descripcionCaja());
        caja.setActiva(request.activa() == null || request.activa());
        return caja;
    }

    public static CajaResDto toResponseDto(Caja caja) {
        return new CajaResDto(
                caja.getIdCaja(),
                caja.getNombreCaja(),
                caja.getMontoInicial(),
                caja.getMoneda(),
                caja.getDescripcionCaja(),
                caja.getActiva(),
                caja.getFechaCreacion()
        );
    }

    public static List<CajaResDto> toResponseDtoList(List<Caja> models) {
        return models.stream()
                .map(CajaMapper::toResponseDto)
                .toList();
    }

}
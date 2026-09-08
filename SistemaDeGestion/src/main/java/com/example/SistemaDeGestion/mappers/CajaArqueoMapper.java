package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.CajaArqueoResDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.models.CajaArqueo;
import com.example.SistemaDeGestion.models.Pedido;

import java.util.List;

public class CajaArqueoMapper {

    public static CajaArqueoResDto toResponseDto(CajaArqueo arqueo) {
        if (arqueo == null) return null;
        return new CajaArqueoResDto(
                arqueo.getIdArqueo(),
                arqueo.getCaja().getIdCaja(),
                arqueo.getCaja().getNombre(),
                arqueo.getMontoInicial(),
                arqueo.getMontoFinal(),
                arqueo.getTotalEfectivo(),
                arqueo.getTotalDebito(),
                arqueo.getTotalCredito(),
                arqueo.getTotalTransferencia(),
                arqueo.getCantidadPedidos(),
                arqueo.getFechaApertura(),
                arqueo.getFechaCierre(),
                null
        );
    }

    public static CajaArqueoResDto toResponseDtoWithMovimientos(CajaArqueo arqueo, List<Pedido> pedidos) {
        if (arqueo == null) return null;
        List<PedidoResDto> movimientos = PedidoMapper.toResponseDtoList(pedidos);
        return new CajaArqueoResDto(
                arqueo.getIdArqueo(),
                arqueo.getCaja().getIdCaja(),
                arqueo.getCaja().getNombre(),
                arqueo.getMontoInicial(),
                arqueo.getMontoFinal(),
                arqueo.getTotalEfectivo(),
                arqueo.getTotalDebito(),
                arqueo.getTotalCredito(),
                arqueo.getTotalTransferencia(),
                arqueo.getCantidadPedidos(),
                arqueo.getFechaApertura(),
                arqueo.getFechaCierre(),
                movimientos
        );
    }

    public static List<CajaArqueoResDto> toResponseDtoList(List<CajaArqueo> arqueos) {
        if (arqueos == null) return List.of();
        return arqueos.stream().map(CajaArqueoMapper::toResponseDto).toList();
    }
}

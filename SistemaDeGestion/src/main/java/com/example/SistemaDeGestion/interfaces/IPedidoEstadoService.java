package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.EstadoPedidoReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;

public interface IPedidoEstadoService {

    PedidoResDto execute(Long pedidoId, EstadoPedidoReqDto request);
}
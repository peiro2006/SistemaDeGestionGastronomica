package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.response.PedidoResDto;

import java.util.List;

public interface IPedidoListService {

    List<PedidoResDto> misPedidos();

    List<PedidoResDto> pedidosPorEstado(String estado);

    List<PedidoResDto> listarTodos();
}
package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.PedidoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;

public interface IPedidoCreateService {

    PedidoResDto execute(PedidoCreateReqDto request);
}
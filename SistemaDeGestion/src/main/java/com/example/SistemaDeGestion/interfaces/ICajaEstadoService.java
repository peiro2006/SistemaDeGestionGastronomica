package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.CajaEstadoReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;

public interface ICajaEstadoService {

    CajaResDto execute(Long idCaja, CajaEstadoReqDto request);
}
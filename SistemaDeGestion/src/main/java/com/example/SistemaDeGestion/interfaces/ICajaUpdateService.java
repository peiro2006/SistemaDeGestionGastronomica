package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.CajaUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;

public interface ICajaUpdateService {

    CajaResDto execute(Long idCaja, CajaUpdateReqDto request);
}
package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;

public interface ICreateCajaService {

    CajaResDto execute(CajaCreateReqDto request);

}
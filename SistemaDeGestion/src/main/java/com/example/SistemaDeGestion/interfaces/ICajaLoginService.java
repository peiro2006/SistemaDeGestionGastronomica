package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.CajaLoginReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaLoginResDto;

public interface ICajaLoginService {

    CajaLoginResDto execute(CajaLoginReqDto request);
}
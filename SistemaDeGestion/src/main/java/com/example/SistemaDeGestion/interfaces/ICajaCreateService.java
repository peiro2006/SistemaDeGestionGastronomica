package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.CajaUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;

import java.util.List;

public interface ICajaCreateService {

    CajaResDto execute(CajaCreateReqDto request);
}
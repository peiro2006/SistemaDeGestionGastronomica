package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;

public interface ICreateProveedorService {

    ProveedorResDto execute(ProveedorCreateReqDto request);

}
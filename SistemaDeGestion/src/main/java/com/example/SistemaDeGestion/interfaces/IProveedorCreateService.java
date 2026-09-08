package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;

import java.util.List;

public interface IProveedorCreateService {
    ProveedorResDto execute(ProveedorCreateReqDto request);
}
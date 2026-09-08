package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;

import java.util.List;

public interface IProveedorUpdateService {
    ProveedorResDto execute(Long idProveedor, ProveedorUpdateReqDto request);
}

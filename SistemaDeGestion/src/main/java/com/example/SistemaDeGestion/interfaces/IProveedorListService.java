package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;

import java.util.List;

public interface IProveedorListService {
    List<ProveedorResDto> listarTodas();

    List<ProveedorResDto> listarActivos();

    ProveedorResDto obtenerPorId(Long idProveedor);
}
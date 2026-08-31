package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.response.CajaResDto;

import java.util.List;

public interface ICajaListService {

    List<CajaResDto> listarTodas();

    List<CajaResDto> listarDisponibles();

    CajaResDto obtenerPorId(Long idCaja);
}
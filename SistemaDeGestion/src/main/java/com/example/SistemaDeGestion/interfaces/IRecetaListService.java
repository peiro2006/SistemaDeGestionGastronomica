package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;

import java.util.List;

public interface IRecetaListService {

    List<RecetaCreateResDto> execute(String nombre);

}

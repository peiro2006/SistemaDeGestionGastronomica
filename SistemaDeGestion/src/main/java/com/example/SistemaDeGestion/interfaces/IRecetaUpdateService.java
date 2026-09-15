package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;

public interface IRecetaUpdateService {

    RecetaCreateResDto execute(Long idReceta, RecetaCreateReqDto request);

}

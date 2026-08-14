package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.UsuarioCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.UsuarioCreateResDto;

public interface IRegistroService {

    UsuarioCreateResDto execute(UsuarioCreateReqDto request);

}

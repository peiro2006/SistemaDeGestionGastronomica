package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.LoginRequestDto;
import com.example.SistemaDeGestion.dtos.response.LoginResponseDto;

public interface ILoginService {

    LoginResponseDto execute(LoginRequestDto request);

}

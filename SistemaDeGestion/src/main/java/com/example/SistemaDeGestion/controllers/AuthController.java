package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.LoginRequestDto;
import com.example.SistemaDeGestion.dtos.request.UsuarioCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.LoginResponseDto;
import com.example.SistemaDeGestion.dtos.response.UsuarioCreateResDto;
import com.example.SistemaDeGestion.interfaces.ILoginService;
import com.example.SistemaDeGestion.interfaces.IRegistroService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final IRegistroService registroService;
    private final ILoginService loginService;

    @PostMapping("/registro")
    public ResponseEntity<BaseResponse<UsuarioCreateResDto>> registrar(
            @Valid @RequestBody UsuarioCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        registroService.execute(request),
                        "Usuario registrado correctamente"
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        loginService.execute(request),
                        "Sesion iniciada correctamente"
                )
        );
    }

}

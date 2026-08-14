package com.example.SistemaDeGestion.dtos.response;

public record LoginResponseDto (

        String token,
        String tokenType,
        long expiresIn,
        UsuarioCreateResDto usuario

) {
}

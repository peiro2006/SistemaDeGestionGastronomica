package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.UsuarioCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.UsuarioCreateResDto;
import com.example.SistemaDeGestion.models.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toModel(UsuarioCreateReqDto request, String encodedPassword, String rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setPassword(encodedPassword);
        usuario.setRol(rol);
        return usuario;
    }

    public static UsuarioCreateResDto toResponseDto(Usuario usuario) {
        return new UsuarioCreateResDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getFechaCreacion()
        );
    }

}
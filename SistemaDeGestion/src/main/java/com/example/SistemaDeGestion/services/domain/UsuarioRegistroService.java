package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.UsuarioCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.UsuarioCreateResDto;
import com.example.SistemaDeGestion.interfaces.IRegistroService;
import com.example.SistemaDeGestion.mappers.UsuarioMapper;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioRegistroService implements IRegistroService {

    private static final String ROL_USUARIO = "ROLE_USER";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioCreateResDto execute(UsuarioCreateReqDto request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ya existe un usuario registrado con el email " + request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Usuario usuario = UsuarioMapper.toModel(request, encodedPassword, ROL_USUARIO);
        return UsuarioMapper.toResponseDto(usuarioRepository.save(usuario));
    }

}

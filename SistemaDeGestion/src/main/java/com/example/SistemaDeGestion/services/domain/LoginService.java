package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.dtos.request.LoginRequestDto;
import com.example.SistemaDeGestion.dtos.response.LoginResponseDto;
import com.example.SistemaDeGestion.dtos.response.UsuarioCreateResDto;
import com.example.SistemaDeGestion.interfaces.ILoginService;
import com.example.SistemaDeGestion.mappers.UsuarioMapper;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import com.example.SistemaDeGestion.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService implements ILoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDto execute(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadRequestException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales invalidas");
        }

        String token = jwtUtil.generateToken(usuario);
        UsuarioCreateResDto userDto = UsuarioMapper.toResponseDto(usuario);

        return new LoginResponseDto(
                token,
                "Bearer",
                jwtUtil.getExpirationSeconds(),
                userDto
        );
    }

}

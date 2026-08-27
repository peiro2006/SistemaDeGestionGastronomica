package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.LoginRequestDto;
import com.example.SistemaDeGestion.dtos.request.UsuarioCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.LoginResponseDto;
import com.example.SistemaDeGestion.dtos.response.UsuarioCreateResDto;
import com.example.SistemaDeGestion.interfaces.ILoginService;
import com.example.SistemaDeGestion.interfaces.IRegistroService;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class AuthController {

    private final IRegistroService registroService;
    private final ILoginService loginService;
    private final UsuarioRepository usuarioRepository;

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

    @PutMapping("/promover-empleado/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UsuarioCreateResDto>> promoverAEmpleado(@PathVariable String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new com.example.SistemaDeGestion.configs.exceptions.NotFoundException("Usuario no encontrado"));
        usuario.setRol("ROLE_EMPLEADO");
        return ResponseEntity.ok(
                BaseResponse.ok(
                        com.example.SistemaDeGestion.mappers.UsuarioMapper.toResponseDto(usuarioRepository.save(usuario)),
                        "Usuario promovido a empleado correctamente"
                )
        );
    }

}

package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.ConfiguracionUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ConfiguracionResDto;
import com.example.SistemaDeGestion.services.domain.ConfiguracionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/configuracion")
@CrossOrigin(origins = "http://localhost:4200")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping("/{clave}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ConfiguracionResDto>> obtener(@PathVariable String clave) {
        return ResponseEntity.ok(
                BaseResponse.ok(configuracionService.obtener(clave), "Configuracion obtenida")
        );
    }

    @PutMapping("/{clave}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ConfiguracionResDto>> actualizar(
            @PathVariable String clave,
            @Valid @RequestBody ConfiguracionUpdateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(configuracionService.actualizar(clave, request.valor()), "Configuracion actualizada")
        );
    }
}

package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.response.ResenaResDto;
import com.example.SistemaDeGestion.services.domain.ResenaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resena")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<ResenaResDto>>> listarTodas() {
        return ResponseEntity.ok(
                BaseResponse.ok(resenaService.listarTodas(), "Reseñas obtenidas correctamente")
        );
    }

    @GetMapping("/filtro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<ResenaResDto>>> listarConFiltros(
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String usuario
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(resenaService.listarConFiltros(producto, fecha, usuario), "Reseñas obtenidas correctamente")
        );
    }
}

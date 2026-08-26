package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.InsumoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.InsumoResDto;
import com.example.SistemaDeGestion.services.domain.InsumoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/insumos")
@AllArgsConstructor
public class InsumosController {

    private final InsumoService insumoService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<InsumoResDto>>> listar() {
        return ResponseEntity.ok(
                BaseResponse.ok(insumoService.listar(), "Insumos obtenidos correctamente")
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<InsumoResDto>> crear(
            @Valid @RequestBody InsumoCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(insumoService.crear(request), "Insumo creado correctamente")
        );
    }

}

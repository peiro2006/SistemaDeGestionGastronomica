package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.interfaces.ICreateCajaService;
import com.example.SistemaDeGestion.services.domain.CajaListService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Caja")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class CajaController {

    private final ICreateCajaService cajaCreateService;
    private final CajaListService cajaListService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CajaResDto>> createCaja(
            @Valid @RequestBody CajaCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        cajaCreateService.execute(request),
                        "Caja creada correctamente"
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<CajaResDto>>> listCajas() {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        cajaListService.execute(),
                        "Cajas obtenidas correctamente"
                )
        );
    }

}
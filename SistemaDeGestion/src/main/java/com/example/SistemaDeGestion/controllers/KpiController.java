package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.services.domain.KpiService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/kpi")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class KpiController {

    private final KpiService kpiService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<Map<String, Object>>> obtenerKPIs() {
        return ResponseEntity.ok(
                BaseResponse.ok(kpiService.obtenerKPIs(), "KPIs obtenidos")
        );
    }
}
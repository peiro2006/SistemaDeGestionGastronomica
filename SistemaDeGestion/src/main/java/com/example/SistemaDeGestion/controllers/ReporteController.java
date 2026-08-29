package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.response.ReporteResDto;
import com.example.SistemaDeGestion.services.domain.ReporteVentasService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Reporte")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ReporteController {

    private final ReporteVentasService reporteVentasService;

    @GetMapping("/pedidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ReporteResDto>> reporteVentas(
            @RequestParam(required = false) Long desde,
            @RequestParam(required = false) Long hasta
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        reporteVentasService.ventas(desde, hasta),
                        "Reporte de pedidos obtenido correctamente"
                )
        );
    }

}
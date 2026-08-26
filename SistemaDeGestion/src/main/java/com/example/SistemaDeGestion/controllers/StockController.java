package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.StockAjusteReqDto;
import com.example.SistemaDeGestion.dtos.response.StockMovimientoResDto;
import com.example.SistemaDeGestion.services.domain.StockMovimientoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/stock/movimientos")
@AllArgsConstructor
public class StockController {

    private final StockMovimientoService stockMovimientoService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<StockMovimientoResDto>>> listar(
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idInsumo
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        stockMovimientoService.listar(idProducto, idInsumo),
                        "Movimientos de stock obtenidos correctamente"
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<StockMovimientoResDto>> ajustar(
            @Valid @RequestBody StockAjusteReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        stockMovimientoService.ajustar(request),
                        "Stock actualizado correctamente"
                )
        );
    }

}

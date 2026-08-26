package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.services.domain.CatalogoProductosService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalogo")
@AllArgsConstructor
public class CatalogoController {

    private final CatalogoProductosService catalogoProductosService;

    @GetMapping("/productos")
    public ResponseEntity<BaseResponse<List<ProductoCreateResDto>>> listarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        catalogoProductosService.execute(nombre, categoria),
                        "Catalogo obtenido correctamente"
                )
        );
    }

}

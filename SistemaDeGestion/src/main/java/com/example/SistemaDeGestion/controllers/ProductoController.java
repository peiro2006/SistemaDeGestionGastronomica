package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.ProductoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.interfaces.ICreateProductoService;
import com.example.SistemaDeGestion.interfaces.IProductoListService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Producto")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ProductoController {

    private final ICreateProductoService productoCreateService;
    private final IProductoListService productoListService;

    @PostMapping
    public ResponseEntity<BaseResponse<ProductoCreateResDto>> createProducto(
            @Valid @RequestBody ProductoCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoCreateService.execute(request),
                        "Producto creado correctamente"
                )
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductoCreateResDto>>> listProductos(
            @RequestParam(required = false) String nombre
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoListService.execute(nombre),
                        "Productos obtenidos correctamente"
                )
        );
    }

}

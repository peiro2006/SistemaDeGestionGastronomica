package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.ProductoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.ProductoEstadoReqDto;
import com.example.SistemaDeGestion.dtos.request.ProductoUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.interfaces.ICreateProductoService;
import com.example.SistemaDeGestion.services.domain.CatalogoProductosService;
import com.example.SistemaDeGestion.services.domain.ProductoEstadoService;
import com.example.SistemaDeGestion.services.domain.ProductoGetService;
import com.example.SistemaDeGestion.services.domain.ProductoUpdateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Producto")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ProductoController {

    private final ICreateProductoService productoCreateService;
    private final CatalogoProductosService catalogoProductosService;
    private final ProductoGetService productoGetService;
    private final ProductoUpdateService productoUpdateService;
    private final ProductoEstadoService productoEstadoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('EMPLEADO') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<ProductoCreateResDto>>> listProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        catalogoProductosService.execute(nombre, categoria),
                        "Productos obtenidos correctamente"
                )
        );
    }

    @GetMapping("/{idProducto}")
    @PreAuthorize("hasRole('USER') or hasRole('EMPLEADO') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProductoCreateResDto>> getProducto(
            @PathVariable Long idProducto
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoGetService.execute(idProducto),
                        "Producto obtenido correctamente"
                )
        );
    }

    @PutMapping("/{idProducto}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProductoCreateResDto>> updateProducto(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoUpdateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoUpdateService.execute(idProducto, request),
                        "Producto actualizado correctamente"
                )
        );
    }

    @PatchMapping("/{idProducto}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProductoCreateResDto>> cambiarEstado(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoEstadoReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoEstadoService.execute(idProducto, request.activo()),
                        "Estado del producto actualizado correctamente"
                )
        );
    }

}

package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.services.domain.ProveedorCreateService;
import com.example.SistemaDeGestion.services.domain.ProveedorListService;
import com.example.SistemaDeGestion.services.domain.ProveedorUpdateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedor")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ProveedorController {

    private final ProveedorCreateService proveedorCreateService;
    private final ProveedorUpdateService proveedorUpdateService;
    private final ProveedorListService proveedorListService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProveedorResDto>> crearProveedor(
            @Valid @RequestBody ProveedorCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(proveedorCreateService.execute(request), "Proveedor creado correctamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<List<ProveedorResDto>>> listarTodas() {
        return ResponseEntity.ok(
                BaseResponse.ok(proveedorListService.listarTodas(), "Proveedores obtenidos")
        );
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<List<ProveedorResDto>>> listarActivos() {
        return ResponseEntity.ok(
                BaseResponse.ok(proveedorListService.listarActivos(), "Proveedores activos obtenidos")
        );
    }

    @GetMapping("/{idProveedor}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProveedorResDto>> obtenerPorId(@PathVariable Long idProveedor) {
        return ResponseEntity.ok(
                BaseResponse.ok(proveedorListService.obtenerPorId(idProveedor), "Proveedor obtenido")
        );
    }

    @PutMapping("/{idProveedor}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProveedorResDto>> actualizar(
            @PathVariable Long idProveedor,
            @Valid @RequestBody ProveedorUpdateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(proveedorUpdateService.execute(idProveedor, request), "Proveedor actualizado")
        );
    }
}
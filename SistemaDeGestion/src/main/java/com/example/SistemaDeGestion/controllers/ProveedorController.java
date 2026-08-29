package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.interfaces.ICreateProveedorService;
import com.example.SistemaDeGestion.services.domain.ProveedorDeleteService;
import com.example.SistemaDeGestion.services.domain.ProveedorListService;
import com.example.SistemaDeGestion.services.domain.ProveedorUpdateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Proveedor")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ProveedorController {

    private final ICreateProveedorService proveedorCreateService;
    private final ProveedorListService proveedorListService;
    private final ProveedorUpdateService proveedorUpdateService;
    private final ProveedorDeleteService proveedorDeleteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProveedorResDto>> createProveedor(
            @Valid @RequestBody ProveedorCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        proveedorCreateService.execute(request),
                        "Proveedor creado correctamente"
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<ProveedorResDto>>> listProveedores() {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        proveedorListService.execute(),
                        "Proveedores obtenidos correctamente"
                )
        );
    }

    @PutMapping("/{idProveedor}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<ProveedorResDto>> updateProveedor(
            @PathVariable Long idProveedor,
            @Valid @RequestBody ProveedorUpdateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        proveedorUpdateService.execute(idProveedor, request),
                        "Proveedor actualizado correctamente"
                )
        );
    }

    @DeleteMapping("/{idProveedor}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteProveedor(
            @PathVariable Long idProveedor
    ) {
        proveedorDeleteService.execute(idProveedor);
        return ResponseEntity.ok(
                BaseResponse.noContent("Proveedor eliminado correctamente")
        );
    }

}
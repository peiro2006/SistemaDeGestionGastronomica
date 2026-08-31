package com.example.SistemaDeGestion.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.CajaEstadoReqDto;
import com.example.SistemaDeGestion.dtos.request.CajaLoginReqDto;
import com.example.SistemaDeGestion.dtos.request.CajaUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaLoginResDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.services.domain.CajaCreateService;
import com.example.SistemaDeGestion.services.domain.CajaEstadoService;
import com.example.SistemaDeGestion.services.domain.CajaListService;
import com.example.SistemaDeGestion.services.domain.CajaLoginService;
import com.example.SistemaDeGestion.services.domain.CajaUpdateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/caja")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class CajaController {

    private final CajaCreateService cajaCreateService;
    private final CajaListService cajaListService;
    private final CajaUpdateService cajaUpdateService;
    private final CajaEstadoService cajaEstadoService;
    private final CajaLoginService cajaLoginService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CajaResDto>> crearCaja(
            @Valid @RequestBody CajaCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaCreateService.execute(request), "Caja creada correctamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<CajaResDto>>> listarTodas() {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaListService.listarTodas(), "Cajas obtenidas correctamente")
        );
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<List<CajaResDto>>> listarDisponibles() {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaListService.listarDisponibles(), "Cajas disponibles obtenidas")
        );
    }

    @GetMapping("/{idCaja}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CajaResDto>> obtenerCaja(@PathVariable Long idCaja) {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaListService.obtenerPorId(idCaja), "Caja obtenida correctamente")
        );
    }

    @PutMapping("/{idCaja}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CajaResDto>> actualizarCaja(
            @PathVariable Long idCaja,
            @Valid @RequestBody CajaUpdateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaUpdateService.execute(idCaja, request), "Caja actualizada correctamente")
        );
    }

    @PutMapping("/{idCaja}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CajaResDto>> cambiarEstado(
            @PathVariable Long idCaja,
            @Valid @RequestBody CajaEstadoReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaEstadoService.execute(idCaja, request), "Estado actualizado correctamente")
        );
    }

    @PostMapping("/login")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<CajaLoginResDto>> loginCaja(
            @Valid @RequestBody CajaLoginReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(cajaLoginService.execute(request), "Caja abierta correctamente")
        );
    }
}
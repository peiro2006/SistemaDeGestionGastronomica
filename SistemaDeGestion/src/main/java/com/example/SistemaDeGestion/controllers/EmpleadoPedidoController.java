package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.EstadoPedidoReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.interfaces.IPedidoEstadoService;
import com.example.SistemaDeGestion.interfaces.IPedidoListService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleado/pedido")
public class EmpleadoPedidoController {

    private final IPedidoListService pedidoListService;
    private final IPedidoEstadoService pedidoEstadoService;

    public EmpleadoPedidoController(IPedidoListService pedidoListService, IPedidoEstadoService pedidoEstadoService) {
        this.pedidoListService = pedidoListService;
        this.pedidoEstadoService = pedidoEstadoService;
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<List<PedidoResDto>>> pedidosPendientes() {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoListService.pedidosPorEstado("PENDIENTE"),
                        "Pedidos pendientes obtenidos correctamente"
                )
        );
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<BaseResponse<PedidoResDto>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoPedidoReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoEstadoService.execute(id, request),
                        "Estado actualizado correctamente"
                )
        );
    }
}
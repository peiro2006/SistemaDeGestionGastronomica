package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.PedidoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.EstadoPedidoReqDto;
import com.example.SistemaDeGestion.dtos.request.ResenaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.dtos.response.ResenaResDto;
import com.example.SistemaDeGestion.services.domain.PedidoCreateService;
import com.example.SistemaDeGestion.services.domain.PedidoEstadoService;
import com.example.SistemaDeGestion.services.domain.PedidoListService;
import com.example.SistemaDeGestion.services.domain.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoCreateService pedidoCreateService;
    private final PedidoListService pedidoListService;
    private final PedidoEstadoService pedidoEstadoService;
    private final ResenaService resenaService;

    public PedidoController(PedidoCreateService pedidoCreateService, PedidoListService pedidoListService,
            PedidoEstadoService pedidoEstadoService, ResenaService resenaService) {
        this.pedidoCreateService = pedidoCreateService;
        this.pedidoListService = pedidoListService;
        this.pedidoEstadoService = pedidoEstadoService;
        this.resenaService = resenaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<PedidoResDto>> crearPedido(
            @Valid @RequestBody PedidoCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(pedidoCreateService.execute(request), "Pedido creado correctamente")
        );
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<List<PedidoResDto>>> misPedidos() {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoListService.misPedidos(),
                        "Pedidos obtenidos correctamente"
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<PedidoResDto>>> listarTodos() {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoListService.listarTodos(),
                        "Pedidos obtenidos correctamente"
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
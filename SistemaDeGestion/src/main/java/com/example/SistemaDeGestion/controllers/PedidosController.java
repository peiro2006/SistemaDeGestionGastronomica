package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.PedidoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.PedidoEstadoReqDto;
import com.example.SistemaDeGestion.dtos.request.ResenaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.dtos.response.ResenaResDto;
import com.example.SistemaDeGestion.services.domain.PedidoCreateService;
import com.example.SistemaDeGestion.services.domain.PedidoEstadoService;
import com.example.SistemaDeGestion.services.domain.PedidoListService;
import com.example.SistemaDeGestion.services.domain.ResenaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
public class PedidosController {

    private final PedidoCreateService pedidoCreateService;
    private final PedidoListService pedidoListService;
    private final PedidoEstadoService pedidoEstadoService;
    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<BaseResponse<PedidoResDto>> crear(
            @Valid @RequestBody PedidoCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(pedidoCreateService.execute(request), "Pedido creado correctamente")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<PedidoResDto>>> listarMisPedidos() {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoListService.listarMisPedidos(),
                        "Pedidos obtenidos correctamente"
                )
        );
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<BaseResponse<PedidoResDto>> obtenerPorId(
            @PathVariable Long idPedido
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoListService.obtenerPorId(idPedido),
                        "Pedido obtenido correctamente"
                )
        );
    }

    @PatchMapping("/{idPedido}/estado")
    public ResponseEntity<BaseResponse<PedidoResDto>> cambiarEstado(
            @PathVariable Long idPedido,
            @Valid @RequestBody PedidoEstadoReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        pedidoEstadoService.cambiarEstado(idPedido, request.estado()),
                        "Estado del pedido actualizado correctamente"
                )
        );
    }

    @PostMapping("/{idPedido}/resena")
    public ResponseEntity<BaseResponse<ResenaResDto>> crearResena(
            @PathVariable Long idPedido,
            @Valid @RequestBody ResenaCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        resenaService.crear(idPedido, request),
                        "Resena creada correctamente"
                )
        );
    }

    @GetMapping("/{idPedido}/resena")
    public ResponseEntity<BaseResponse<List<ResenaResDto>>> listarResenaPorPedido(
            @PathVariable Long idPedido
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        resenaService.listarPorPedido(idPedido),
                        "Resenas obtenidas correctamente"
                )
        );
    }

}

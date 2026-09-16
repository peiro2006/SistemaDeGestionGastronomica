package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.EstadoPedidoReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.interfaces.IPedidoEstadoService;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.*;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.repositories.InsumosRepository;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PedidoEstadoService implements IPedidoEstadoService {

    private final PedidoRepository pedidoRepository;
    private final ProductosRepository productosRepository;
    private final CajaRepository cajaRepository;
    private final InsumosRepository insumosRepository;

    @Override
    @Transactional
    public PedidoResDto execute(Long pedidoId, EstadoPedidoReqDto request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado con id " + pedidoId));

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(request.estado().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido: " + request.estado() +
                    ". Estados válidos: pendiente, en_preparacion, enviado, entregado, cancelado");
        }

        // Validar transición de estado válida
        if (!esTransicionValida(pedido.getEstado(), nuevoEstado)) {
            throw new BadRequestException("Transición de estado no permitida: de " +
                    pedido.getEstado() + " a " + nuevoEstado);
        }

        EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);
        pedido.setFechaActualizacion(Instant.now());

        // Descontar stock solo cuando el empleado confirma que el pedido esta hecho (entregado)
        if (nuevoEstado == EstadoPedido.entregado && estadoAnterior != EstadoPedido.entregado) {
            descontarStock(pedido);
            sumarAMontoCaja(pedido);
        }

        // Restaurar stock de producto si se cancela el pedido
        if (nuevoEstado == EstadoPedido.cancelado && estadoAnterior != EstadoPedido.cancelado) {
            restaurarStockProducto(pedido);
        }

        return PedidoMapper.toResponseDto(pedidoRepository.save(pedido));
    }

    private void descontarStock(Pedido pedido) {
        List<Insumo> insumosActualizar = new ArrayList<>();

        for (PedidoItem item : pedido.getItems()) {
            Producto producto = item.getProducto();
            int cantidadPedido = item.getCantidad() != null ? item.getCantidad() : 0;

            if (producto.getReceta() == null || producto.getReceta().getIngredientes() == null) {
                continue;
            }

            for (RecetaInsumo recetaInsumo : producto.getReceta().getIngredientes()) {
                Insumo insumo = insumosRepository.findByIdForUpdate(recetaInsumo.getInsumo().getIdInsumo())
                        .orElse(null);
                if (insumo == null) continue;

                BigDecimal cantidadNecesaria = recetaInsumo.getCantidad().multiply(BigDecimal.valueOf(cantidadPedido));
                int stockActual = insumo.getStockActual() != null ? insumo.getStockActual() : 0;
                if (stockActual < cantidadNecesaria.intValue()) {
                    throw new BadRequestException(
                            "Stock insuficiente de insumo " + insumo.getNombreInsumo() +
                            ". Disponible: " + stockActual +
                            ", requerido: " + cantidadNecesaria.stripTrailingZeros().toPlainString()
                    );
                }
                int nuevoStock = stockActual - cantidadNecesaria.intValue();
                insumo.setStockActual(nuevoStock);
                insumosActualizar.add(insumo);
            }
        }

        if (!insumosActualizar.isEmpty()) {
            insumosRepository.saveAll(insumosActualizar);
        }
    }

    private void restaurarStockProducto(Pedido pedido) {
        List<Producto> productosActualizar = new ArrayList<>();

        for (PedidoItem item : pedido.getItems()) {
            Producto producto = productosRepository.findByIdForUpdate(item.getProducto().getIdProducto())
                    .orElse(null);
            if (producto == null) continue;

            int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
            int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
            producto.setStockActual(stockActual + cantidad);
            productosActualizar.add(producto);
        }

        if (!productosActualizar.isEmpty()) {
            productosRepository.saveAll(productosActualizar);
        }
    }

    private void sumarAMontoCaja(Pedido pedido) {
        if (pedido.getCaja() != null && pedido.getMetDePago() == MetodoPago.EFECTIVO) {
            var caja = cajaRepository.findById(pedido.getCaja().getIdCaja()).orElse(null);
            if (caja != null) {
                caja.setMontoActual(caja.getMontoActual().add(pedido.getTotal()));
                cajaRepository.save(caja);
            }
        }
    }

    private boolean esTransicionValida(EstadoPedido actual, EstadoPedido nuevo) {
        // Flujo sugerido: pendiente -> en_preparacion -> enviado -> entregado
        // Se permite marcar como entregado (hecho) o cancelado desde cualquier estado activo
        return switch (actual) {
            case pendiente -> nuevo != actual;
            case en_preparacion -> nuevo != actual;
            case enviado -> nuevo != actual;
            case entregado -> false; // No se puede cambiar desde entregado
            case cancelado -> false; // No se puede cambiar desde cancelado
        };
    }
}
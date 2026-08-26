package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.PedidoItemResDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.PedidoItem;

import java.util.List;

public class PedidoMapper {

    private PedidoMapper() {
    }

    public static PedidoResDto toResponseDto(Pedido pedido) {
        return new PedidoResDto(
                pedido.getIdPedido(),
                pedido.getEstado().name(),
                pedido.getTotal(),
                pedido.getFechaCreacion(),
                toItemResponseList(pedido.getItems())
        );
    }

    private static List<PedidoItemResDto> toItemResponseList(List<PedidoItem> items) {
        return items.stream()
                .map(item -> new PedidoItemResDto(
                        item.getIdPedidoItem(),
                        item.getProducto().getIdProducto(),
                        item.getProducto().getNombreProducto(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                ))
                .toList();
    }

}

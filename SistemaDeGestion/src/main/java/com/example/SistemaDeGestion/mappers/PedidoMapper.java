package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.PedidoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.PedidoItemReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoItemResDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.PedidoItem;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Usuario;

import java.math.BigDecimal;
import java.util.List;

public class PedidoMapper {

    private PedidoMapper() {
    }

    public static Pedido toModel(PedidoCreateReqDto request, Usuario usuario) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.pendiente);
        pedido.setMetDePago(request.metDePago());
        pedido.setTotal(BigDecimal.ZERO);
        return pedido;
    }

    public static PedidoItem toItemModel(PedidoItemReqDto itemDto, Producto producto) {
        BigDecimal precio = new BigDecimal(producto.getPrecio());
        PedidoItem item = new PedidoItem();
        item.setProducto(producto);
        item.setCantidad(itemDto.cantidad());
        item.setPrecioUnitario(precio);
        item.setSubtotal(precio.multiply(BigDecimal.valueOf(itemDto.cantidad())));
        return item;
    }

    public static PedidoResDto toResponseDto(Pedido pedido) {
        List<PedidoItemResDto> itemsDto = pedido.getItems().stream()
                .map(PedidoMapper::toItemResponseDto)
                .toList();

        return new PedidoResDto(
                pedido.getIdPedido(),
                pedido.getUsuario().getIdUsuario(),
                pedido.getCaja() != null ? pedido.getCaja().getIdCaja() : null,
                pedido.getEstado().name(),
                pedido.getMetDePago() != null ? pedido.getMetDePago().name() : "EFECTIVO",
                pedido.getTotal(),
                pedido.getFechaCreacion(),
                pedido.getFechaActualizacion(),
                itemsDto
        );
    }

    public static PedidoItemResDto toItemResponseDto(PedidoItem item) {
        return new PedidoItemResDto(
                item.getIdPedidoItem(),
                item.getProducto().getIdProducto(),
                item.getProducto().getNombreProducto(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
        );
    }

    public static List<PedidoResDto> toResponseDtoList(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(PedidoMapper::toResponseDto)
                .toList();
    }
}
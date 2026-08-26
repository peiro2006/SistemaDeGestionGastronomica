package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.PedidoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.PedidoItemCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.PedidoItem;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.PedidosRepository;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PedidoCreateService {

    private final PedidosRepository pedidosRepository;
    private final ProductosRepository productosRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Transactional
    public PedidoResDto execute(PedidoCreateReqDto request) {
        Usuario cliente = obtenerUsuarioAutenticado();
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .estado(EstadoPedido.CREADO)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoItemCreateReqDto itemRequest : request.items()) {
            Producto producto = productosRepository.findByIdForUpdate(itemRequest.idProducto())
                    .orElseThrow(() -> new NotFoundException("No existe un producto con el id " + itemRequest.idProducto()));
            validarProductoDisponible(producto, itemRequest.cantidad());

            producto.setStockActual(producto.getStockActual() - itemRequest.cantidad());
            notificacionService.verificarStockBajo(producto.getIdProducto());
            BigDecimal precioUnitario = obtenerPrecio(producto);
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(itemRequest.cantidad()));
            total = total.add(subtotal);

            pedido.getItems().add(
                    PedidoItem.builder()
                            .pedido(pedido)
                            .producto(producto)
                            .cantidad(itemRequest.cantidad())
                            .precioUnitario(precioUnitario)
                            .subtotal(subtotal)
                            .build()
            );
        }

        pedido.setTotal(total);
        return PedidoMapper.toResponseDto(pedidosRepository.save(pedido));
    }

    private void validarProductoDisponible(Producto producto, Integer cantidadSolicitada) {
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new BadRequestException("El producto " + producto.getNombreProducto() + " no esta disponible");
        }
        int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();
        if (stockActual < cantidadSolicitada) {
            throw new BadRequestException(
                    "Stock insuficiente para " + producto.getNombreProducto() + ". Disponible: " + stockActual
            );
        }
    }

    private BigDecimal obtenerPrecio(Producto producto) {
        try {
            return new BigDecimal(producto.getPrecio());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El producto " + producto.getNombreProducto() + " tiene un precio invalido");
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("No se pudo identificar al usuario autenticado");
        }
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new NotFoundException("No existe el usuario autenticado"));
    }

}

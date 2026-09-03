package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.PedidoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.PedidoItemReqDto;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.interfaces.IPedidoCreateService;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.models.PedidoItem;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PedidoCreateService implements IPedidoCreateService {

    private final PedidoRepository pedidoRepository;
    private final ProductosRepository productosRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaRepository cajaRepository;

    public PedidoCreateService(PedidoRepository pedidoRepository, ProductosRepository productosRepository,
            UsuarioRepository usuarioRepository, CajaRepository cajaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productosRepository = productosRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaRepository = cajaRepository;
    }

    @Override
    @Transactional
    public PedidoResDto execute(PedidoCreateReqDto request) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return executeConUsuario(request, usuario);
    }

    @Transactional
    public PedidoResDto executeConUsuario(PedidoCreateReqDto request, Usuario usuario) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new BadRequestException("Debe agregar al menos un producto al pedido");
        }

        Pedido pedido = PedidoMapper.toModel(request, usuario);
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItemReqDto itemDto : request.items()) {
            Producto producto = productosRepository.findById(itemDto.idProducto())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado con id " + itemDto.idProducto()));

            if (producto.getActivo() != null && !producto.getActivo()) {
                throw new BadRequestException("El producto " + producto.getNombreProducto() + " no está disponible");
            }

            Integer stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
            if (stockActual < itemDto.cantidad()) {
                throw new BadRequestException("Stock insuficiente para " + producto.getNombreProducto() +
                        ". Disponible: " + stockActual + ", solicitado: " + itemDto.cantidad());
            }

            PedidoItem item = PedidoMapper.toItemModel(itemDto, producto);
            // Defensa: asegurar que precioUnitario y subtotal no sean null
            if (item.getPrecioUnitario() == null) {
                BigDecimal precio = new BigDecimal(producto.getPrecio());
                item.setPrecioUnitario(precio);
            }
            if (item.getSubtotal() == null) {
                item.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(itemDto.cantidad())));
            }
            pedido.addItem(item);
            total = total.add(item.getSubtotal());
        }

        pedido.setTotal(total);
        Pedido guardado = pedidoRepository.save(pedido);

        // Asignar automáticamente la caja activa (solo puede haber una)
        cajaRepository.findFirstByEstadoOrderByFechaCreacionDesc(EstadoCaja.ACTIVA)
                .ifPresent(caja -> {
                    guardado.setCaja(caja);
                    pedidoRepository.save(guardado);
                });

        return PedidoMapper.toResponseDto(guardado);
    }
}
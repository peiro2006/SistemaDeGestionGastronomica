package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.StockAjusteReqDto;
import com.example.SistemaDeGestion.dtos.response.StockMovimientoResDto;
import com.example.SistemaDeGestion.mappers.StockMovimientoMapper;
import com.example.SistemaDeGestion.models.Insumo;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.StockMovimiento;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.InsumosRepository;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.StockMovimientosRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class StockMovimientoService {

    private static final String TIPO_INGRESO = "INGRESO";
    private static final String TIPO_EGRESO = "EGRESO";

    private final ProductosRepository productosRepository;
    private final InsumosRepository insumosRepository;
    private final StockMovimientosRepository stockMovimientosRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public List<StockMovimientoResDto> listar(Long idProducto, Long idInsumo) {
        if (idProducto != null && idInsumo != null) {
            throw new BadRequestException("Debe filtrar por producto o por insumo, no ambos");
        }
        if (idProducto != null) {
            return StockMovimientoMapper.toResponseDtoList(
                    stockMovimientosRepository.findByProductoIdProductoOrderByFechaDesc(idProducto)
            );
        }
        if (idInsumo != null) {
            return StockMovimientoMapper.toResponseDtoList(
                    stockMovimientosRepository.findByInsumoIdInsumoOrderByFechaDesc(idInsumo)
            );
        }
        return StockMovimientoMapper.toResponseDtoList(stockMovimientosRepository.findAllByOrderByFechaDesc());
    }

    @Transactional
    public StockMovimientoResDto ajustar(StockAjusteReqDto request) {
        validarDestino(request);
        String tipo = normalizarTipo(request.tipo());
        Usuario usuario = obtenerUsuarioAutenticado();

        if (request.idProducto() != null) {
            Producto producto = productosRepository.findByIdForUpdate(request.idProducto())
                    .orElseThrow(() -> new NotFoundException("No existe un producto con el id " + request.idProducto()));
            Integer saldoPosterior = calcularSaldo(producto.getStockActual(), request.cantidad(), tipo);
            producto.setStockActual(saldoPosterior);
            notificacionService.verificarStockBajo(producto.getIdProducto());
            return StockMovimientoMapper.toResponseDto(stockMovimientosRepository.save(
                    crearStockMovimiento(producto, null, tipo, request.cantidad(), request.motivo(), saldoPosterior, usuario)
            ));
        }

        Insumo insumo = insumosRepository.findByIdForUpdate(request.idInsumo())
                .orElseThrow(() -> new NotFoundException("No existe un insumo con el id " + request.idInsumo()));
        Integer saldoPosterior = calcularSaldo(insumo.getStockActual(), request.cantidad(), tipo);
        insumo.setStockActual(saldoPosterior);
        return StockMovimientoMapper.toResponseDto(stockMovimientosRepository.save(
                crearStockMovimiento(null, insumo, tipo, request.cantidad(), request.motivo(), saldoPosterior, usuario)
        ));
    }

    private StockMovimiento crearStockMovimiento(Producto producto, Insumo insumo, String tipo,
            Integer cantidad, String motivo, Integer saldoPosterior, Usuario usuario) {
        StockMovimiento sm = new StockMovimiento();
        sm.setProducto(producto);
        sm.setInsumo(insumo);
        sm.setTipo(tipo);
        sm.setCantidad(cantidad);
        sm.setMotivo(motivo);
        sm.setSaldoPosterior(saldoPosterior);
        sm.setUsuario(usuario);
        return sm;
    }

    private void validarDestino(StockAjusteReqDto request) {
        if ((request.idProducto() == null && request.idInsumo() == null)
                || (request.idProducto() != null && request.idInsumo() != null)) {
            throw new BadRequestException("Debe seleccionar un producto o un insumo");
        }
    }

    private String normalizarTipo(String tipo) {
        String normalizado = tipo.trim().toUpperCase();
        if (!TIPO_INGRESO.equals(normalizado) && !TIPO_EGRESO.equals(normalizado)) {
            throw new BadRequestException("El tipo de movimiento debe ser INGRESO o EGRESO");
        }
        return normalizado;
    }

    private Integer calcularSaldo(Integer stockActual, Integer cantidad, String tipo) {
        int saldoActual = stockActual == null ? 0 : stockActual;
        int saldoPosterior = TIPO_INGRESO.equals(tipo) ? saldoActual + cantidad : saldoActual - cantidad;
        if (saldoPosterior < 0) {
            throw new BadRequestException("El ajuste no puede dejar stock negativo");
        }
        return saldoPosterior;
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

package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.ResenaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ResenaResDto;
import com.example.SistemaDeGestion.mappers.ResenaMapper;
import com.example.SistemaDeGestion.models.*;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import com.example.SistemaDeGestion.repositories.ResenasRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ResenaService {

    private final ResenasRepository resenasRepository;
    private final PedidoRepository pedidosRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ResenaResDto crear(Long idPedido, ResenaCreateReqDto request) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Pedido pedido = pedidosRepository.findById(idPedido)
                .orElseThrow(() -> new NotFoundException("No existe un pedido con el id " + idPedido));

        if (!pedido.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new BadRequestException("El pedido no pertenece al usuario autenticado");
        }

        if (pedido.getEstado() != EstadoPedido.entregado) {
            throw new BadRequestException("Solo puede reseñar pedidos en estado ENTREGADO");
        }

        if (resenasRepository.existsByPedidoIdPedido(idPedido)) {
            throw new ConflictException("Ya existe una reseña para este pedido");
        }

        Resena resena = new Resena();
        resena.setPedido(pedido);
        resena.setUsuario(usuario);
        resena.setCalificacion(request.calificacion());
        resena.setComentario(request.comentario());

        return ResenaMapper.toResponseDto(resenasRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public List<ResenaResDto> listarPorPedido(Long idPedido) {
        return resenasRepository.findByPedidoIdPedido(idPedido)
                .map(resena -> List.of(ResenaMapper.toResponseDto(resena)))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<ResenaResDto> listarPorUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return ResenaMapper.toResponseDtoList(
                resenasRepository.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(usuario.getIdUsuario())
        );
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

package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.interfaces.IPedidoListService;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoListService implements IPedidoListService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoListService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioAutenticado() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public List<PedidoResDto> misPedidos() {
        Usuario usuario = getUsuarioAutenticado();
        return PedidoMapper.toResponseDtoList(pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario));
    }

    @Override
    public List<PedidoResDto> pedidosPorEstado(String estado) {
        try {
            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toLowerCase());
            return PedidoMapper.toResponseDtoList(pedidoRepository.findByEstadoOrderByFechaCreacionAsc(estadoPedido));
        } catch (IllegalArgumentException e) {
            throw new com.example.SistemaDeGestion.configs.exceptions.BadRequestException("Estado inválido: " + estado);
        }
    }

    @Override
    public List<PedidoResDto> listarTodos() {
        return PedidoMapper.toResponseDtoList(
                pedidoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion")));
    }
}
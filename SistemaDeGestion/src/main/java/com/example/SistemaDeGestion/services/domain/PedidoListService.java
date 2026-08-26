package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.PedidosRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PedidoListService {

    private final PedidosRepository pedidosRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PedidoResDto> listarMisPedidos() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return pedidosRepository.findByClienteIdUsuarioOrderByFechaCreacionDesc(usuario.getIdUsuario())
                .stream()
                .map(PedidoMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResDto obtenerPorId(Long idPedido) {
        return pedidosRepository.findById(idPedido)
                .map(PedidoMapper::toResponseDto)
                .orElseThrow(() -> new NotFoundException("No existe un pedido con el id " + idPedido));
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

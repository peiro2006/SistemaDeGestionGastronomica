package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.repositories.PedidosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PedidoEstadoService {

    private final PedidosRepository pedidosRepository;

    @Transactional
    public PedidoResDto cambiarEstado(Long idPedido, String nuevoEstado) {
        Pedido pedido = pedidosRepository.findById(idPedido)
                .orElseThrow(() -> new NotFoundException("No existe un pedido con el id " + idPedido));

        EstadoPedido estado;
        try {
            estado = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado invalido. Valores permitidos: CREADO, ENTREGADO, CANCELADO");
        }

        pedido.setEstado(estado);
        return PedidoMapper.toResponseDto(pedidosRepository.save(pedido));
    }

}

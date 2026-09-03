package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.PedidoResDto;
import com.example.SistemaDeGestion.mappers.PedidoMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CajaMovimientosService {

    private final CajaRepository cajaRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResDto> execute(Long idCaja) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new NotFoundException("No existe una caja con el id " + idCaja));

        List<Pedido> pedidos = pedidoRepository.findByCajaOrderByFechaCreacionDesc(caja);
        return PedidoMapper.toResponseDtoList(pedidos);
    }
}

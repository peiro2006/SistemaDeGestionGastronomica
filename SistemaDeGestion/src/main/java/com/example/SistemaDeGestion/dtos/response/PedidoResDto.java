package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResDto(

        Long idPedido,
        String estado,
        BigDecimal total,
        Instant fechaCreacion,
        List<PedidoItemResDto> items

) {
}

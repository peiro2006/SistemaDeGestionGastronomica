package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;

public record PedidoItemResDto(

        Long idPedidoItem,
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal

) {
}

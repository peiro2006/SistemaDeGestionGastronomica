package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.dtos.response.ReporteResDto;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReporteVentasService {

    private static final String COMPLETADO = "entregado";
    private static final long DIAS_POR_DEFECTO = 30;

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public ReporteResDto ventas(Long desdeMillis, Long hastaMillis) {
        Instant desde = desdeMillis != null
                ? Instant.ofEpochMilli(desdeMillis)
                : Instant.now().minus(DIAS_POR_DEFECTO, ChronoUnit.DAYS);
        Instant hasta = hastaMillis != null
                ? Instant.ofEpochMilli(hastaMillis)
                : Instant.now();

        if (desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        List<Pedido> pedidos = pedidoRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(desde, hasta);

        Map<EstadoPedido, Long> porEstado = new EnumMap<>(EstadoPedido.class);
        BigDecimal totalFacturado = BigDecimal.ZERO;
        long completados = 0;

        for (Pedido pedido : pedidos) {
            porEstado.merge(pedido.getEstado(), 1L, Long::sum);
            if (COMPLETADO.equals(pedido.getEstado().name())) {
                completados++;
                totalFacturado = totalFacturado.add(
                        pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal()
                );
            }
        }

        for (EstadoPedido estado : EstadoPedido.values()) {
            porEstado.putIfAbsent(estado, 0L);
        }

        BigDecimal promedio = completados > 0
                ? totalFacturado.divide(BigDecimal.valueOf(completados), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new ReporteResDto(
                desde,
                hasta,
                totalFacturado,
                promedio,
                completados,
                pedidos.size(),
                porEstado
        );
    }

}
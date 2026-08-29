package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.dtos.response.ReporteResDto;
import com.example.SistemaDeGestion.models.EstadoPedido;
import com.example.SistemaDeGestion.models.Pedido;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class ReporteVentasServiceTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final ReporteVentasService service = new ReporteVentasService(pedidoRepository);

    private Pedido pedido(EstadoPedido estado, String total) {
        Pedido pedido = new Pedido();
        pedido.setEstado(estado);
        pedido.setTotal(new BigDecimal(total));
        return pedido;
    }

    @Test
    void calculaMetricasSoloConPedidosEntregados() {
        Instant ahora = Instant.now();
        when(pedidoRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(any(), any()))
                .thenReturn(List.of(
                        pedido(EstadoPedido.entregado, "1000"),
                        pedido(EstadoPedido.entregado, "1500"),
                        pedido(EstadoPedido.pendiente, "500")
                ));

        ReporteResDto reporte = service.ventas(ahora.toEpochMilli(), ahora.toEpochMilli());

        assertEquals(new BigDecimal("2500"), reporte.totalFacturado());
        assertEquals(2, reporte.cantidadPedidosCompletados());
        assertEquals(3, reporte.cantidadPedidosPeriodo());
        assertEquals(new BigDecimal("1250.00"), reporte.promedioValorPorPedido());
        assertEquals(2L, reporte.pedidosPorEstado().get(EstadoPedido.entregado));
        assertEquals(1L, reporte.pedidosPorEstado().get(EstadoPedido.pendiente));
        assertEquals(0L, reporte.pedidosPorEstado().get(EstadoPedido.cancelado));
    }

    @Test
    void usaUltimos30DiasCuandoNoSeEnviaRango() {
        service.ventas(null, null);

        verify(pedidoRepository).findByFechaCreacionBetweenOrderByFechaCreacionDesc(
                argThat(desde -> desde.isAfter(Instant.now().minus(31, ChronoUnit.DAYS))),
                any(Instant.class)
        );
    }

    @Test
    void devuelveCerosSinPedidos() {
        when(pedidoRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(any(), any()))
                .thenReturn(List.of());

        ReporteResDto reporte = service.ventas(Instant.now().toEpochMilli(), Instant.now().toEpochMilli());

        assertEquals(BigDecimal.ZERO, reporte.totalFacturado());
        assertEquals(0, reporte.cantidadPedidosCompletados());
        assertEquals(0, reporte.cantidadPedidosPeriodo());
        assertEquals(BigDecimal.ZERO, reporte.promedioValorPorPedido());
    }

    @Test
    void rechazaRangoInvertido() {
        assertThrows(BadRequestException.class,
                () -> service.ventas(Instant.now().toEpochMilli(), Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()));
    }
}
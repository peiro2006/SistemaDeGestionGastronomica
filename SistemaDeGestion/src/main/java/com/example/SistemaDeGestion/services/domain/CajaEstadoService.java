package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.CajaEstadoReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.interfaces.ICajaEstadoService;
import com.example.SistemaDeGestion.mappers.CajaMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.CajaArqueo;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.models.MetodoPago;
import com.example.SistemaDeGestion.repositories.CajaArqueoRepository;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@AllArgsConstructor
public class CajaEstadoService implements ICajaEstadoService {

    private final CajaRepository cajaRepository;
    private final CajaArqueoRepository cajaArqueoRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public CajaResDto execute(Long idCaja, CajaEstadoReqDto request) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new NotFoundException("Caja no encontrada con id " + idCaja));

        EstadoCaja nuevoEstado;
        try {
            nuevoEstado = EstadoCaja.valueOf(request.estado().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido: " + request.estado() +
                    ". Estados válidos: INACTIVA, ACTIVA, NO_DISPONIBLE");
        }

        // Validar transiciones
        if (!esTransicionValida(caja.getEstado(), nuevoEstado, caja)) {
            throw new BadRequestException("Transición no permitida: de " + caja.getEstado() + " a " + nuevoEstado);
        }

        EstadoCaja estadoAnterior = caja.getEstado();
        caja.setEstado(nuevoEstado);
        caja.setFechaActualizacion(Instant.now());

        // Si se abre (ACTIVA), desactivar la caja que esté activa (solo una a la vez)
        if (nuevoEstado == EstadoCaja.ACTIVA && estadoAnterior != EstadoCaja.ACTIVA) {
            cajaRepository.findFirstByEstadoOrderByFechaCreacionDesc(EstadoCaja.ACTIVA).ifPresent(activa -> {
                if (!activa.getIdCaja().equals(caja.getIdCaja())) {
                    activa.setEstado(EstadoCaja.INACTIVA);
                    activa.setAbiertaPor(null);
                    activa.setFechaApertura(null);
                    activa.setFechaActualizacion(Instant.now());
                    cajaRepository.save(activa);
                }
            });
            caja.setFechaApertura(Instant.now());
        }

        // Si se cierra (INACTIVA desde ACTIVA), crear arqueo y actualizar montoInicial
        if (nuevoEstado == EstadoCaja.INACTIVA && estadoAnterior == EstadoCaja.ACTIVA) {
            crearArqueo(caja);

            // El admin indica cuánto efectivo deja en la caja para la próxima apertura
            if (request.montoInicial() != null) {
                caja.setMontoInicial(request.montoInicial());
                caja.setMontoActual(request.montoInicial());
            }

            caja.setAbiertaPor(null);
            caja.setFechaApertura(null);
        }

        return CajaMapper.toResponseDto(cajaRepository.save(caja));
    }

    private void crearArqueo(Caja caja) {
        BigDecimal efectivo = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.EFECTIVO);
        BigDecimal debito = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.DEBITO);
        BigDecimal credito = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.TARJETA_CREDITO);
        BigDecimal transferencia = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.TRANSFERENCIA);

        long cantidadPedidos = pedidoRepository.findByCajaOrderByFechaCreacionDesc(caja).stream()
                .filter(p -> p.getEstado() != null && p.getEstado() != com.example.SistemaDeGestion.models.EstadoPedido.cancelado)
                .count();

        CajaArqueo arqueo = CajaArqueo.builder()
                .caja(caja)
                .montoInicial(caja.getMontoInicial())
                .montoFinal(caja.getMontoActual())
                .totalEfectivo(efectivo)
                .totalDebito(debito)
                .totalCredito(credito)
                .totalTransferencia(transferencia)
                .cantidadPedidos((int) cantidadPedidos)
                .fechaApertura(caja.getFechaApertura())
                .fechaCierre(Instant.now())
                .cerradoPor(caja.getAbiertaPor())
                .build();

        cajaArqueoRepository.save(arqueo);
    }

    private boolean esTransicionValida(EstadoCaja actual, EstadoCaja nuevo, Caja caja) {
        if (actual == nuevo) return false;

        // NO_DISPONIBLE solo se sale si tiene fondos
        if (actual == EstadoCaja.NO_DISPONIBLE && !caja.tieneFondos()) {
            return false;
        }

        // No se puede activar si no tiene fondos
        if (nuevo == EstadoCaja.ACTIVA && !caja.tieneFondos()) {
            return false;
        }

        // Transiciones permitidas
        return switch (actual) {
            case INACTIVA -> nuevo == EstadoCaja.ACTIVA || nuevo == EstadoCaja.NO_DISPONIBLE;
            case ACTIVA -> nuevo == EstadoCaja.INACTIVA || nuevo == EstadoCaja.NO_DISPONIBLE;
            case NO_DISPONIBLE -> nuevo == EstadoCaja.INACTIVA; // solo si tiene fondos
        };
    }
}
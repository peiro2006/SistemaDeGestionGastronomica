package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.CajaResumenDto;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.MetodoPago;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.repositories.PedidoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class CajaResumenService {

    private final CajaRepository cajaRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public CajaResumenDto execute(Long idCaja) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new NotFoundException("No existe una caja con el id " + idCaja));

        BigDecimal efectivo = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.EFECTIVO);
        BigDecimal debito = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.DEBITO);
        BigDecimal credito = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.TARJETA_CREDITO);
        BigDecimal transferencia = pedidoRepository.sumTotalByCajaAndMetDePago(caja, MetodoPago.TRANSFERENCIA);

        BigDecimal noEfectivo = debito.add(credito).add(transferencia);

        return new CajaResumenDto(
                efectivo,
                debito,
                credito,
                transferencia,
                noEfectivo,
                caja.getMontoActual()
        );
    }
}

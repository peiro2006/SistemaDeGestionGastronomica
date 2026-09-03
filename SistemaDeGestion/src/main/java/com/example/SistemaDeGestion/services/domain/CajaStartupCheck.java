package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CajaStartupCheck implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CajaStartupCheck.class);
    private final CajaRepository cajaRepository;

    public CajaStartupCheck(CajaRepository cajaRepository) {
        this.cajaRepository = cajaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Caja> activas = cajaRepository.findByEstadoOrderByFechaCreacionDesc(EstadoCaja.ACTIVA);

        if (activas.isEmpty()) {
            logger.info("No hay cajas activas al iniciar");
            return;
        }

        if (activas.size() == 1) {
            logger.info("Caja activa correcta: {}", activas.get(0).getNombre());
            return;
        }

        logger.warn("Se encontraron {} cajas activas. Desactivando todas excepto la más reciente...", activas.size());

        activas.sort((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()));
        Caja masReciente = activas.get(0);

        for (Caja caja : activas) {
            if (!caja.getIdCaja().equals(masReciente.getIdCaja())) {
                caja.setEstado(EstadoCaja.INACTIVA);
                cajaRepository.save(caja);
                logger.info("Caja '{}' desactivada por tener múltiples cajas activas", caja.getNombre());
            }
        }

        logger.info("Caja activa final: {}", masReciente.getNombre());
    }
}

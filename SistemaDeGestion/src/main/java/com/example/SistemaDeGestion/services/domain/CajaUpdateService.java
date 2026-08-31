package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.CajaUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.interfaces.ICajaUpdateService;
import com.example.SistemaDeGestion.mappers.CajaMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class CajaUpdateService implements ICajaUpdateService {

    private final CajaRepository cajaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CajaResDto execute(Long idCaja, CajaUpdateReqDto request) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new NotFoundException("Caja no encontrada con id " + idCaja));

        if (request.nombre() != null && !request.nombre().equals(caja.getNombre())) {
            if (cajaRepository.existsByNombreIgnoreCase(request.nombre())) {
                throw new BadRequestException("Ya existe una caja con ese nombre");
            }
            caja.setNombre(request.nombre());
        }

        if (request.descripcion() != null) {
            caja.setDescripcion(request.descripcion());
        }

        if (request.montoInicial() != null) {
            // Ajustar monto actual proporcionalmente si se cambia el inicial
            BigDecimal diferencia = request.montoInicial().subtract(caja.getMontoInicial());
            caja.setMontoInicial(request.montoInicial());
            caja.setMontoActual(caja.getMontoActual().add(diferencia));
            
            // Auto: si monto actual es 0, no disponible
            if (caja.getMontoActual().compareTo(BigDecimal.ZERO) == 0) {
                caja.setEstado(EstadoCaja.NO_DISPONIBLE);
            }
        }

        if (request.password() != null && !request.password().isBlank()) {
            caja.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return CajaMapper.toResponseDto(cajaRepository.save(caja));
    }
}
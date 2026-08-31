package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.interfaces.ICajaCreateService;
import com.example.SistemaDeGestion.mappers.CajaMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class CajaCreateService implements ICajaCreateService {

    private final CajaRepository cajaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CajaResDto execute(CajaCreateReqDto request) {
        if (cajaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new BadRequestException("Ya existe una caja con ese nombre");
        }

        Caja caja = new Caja();
        caja.setNombre(request.nombre());
        caja.setDescripcion(request.descripcion());
        caja.setMoneda(request.moneda());
        caja.setMontoInicial(request.montoInicial());
        caja.setMontoActual(request.montoInicial());
        caja.setPasswordHash(passwordEncoder.encode(request.password()));
        
        // Estado inicial: INACTIVA, pero si monto es 0 -> NO_DISPONIBLE
        if (request.montoInicial() != null && request.montoInicial().compareTo(BigDecimal.ZERO) == 0) {
            caja.setEstado(EstadoCaja.NO_DISPONIBLE);
        } else {
            caja.setEstado(EstadoCaja.INACTIVA);
        }

        return CajaMapper.toResponseDto(cajaRepository.save(caja));
    }
}
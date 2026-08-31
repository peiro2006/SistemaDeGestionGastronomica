package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.CajaLoginReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaLoginResDto;
import com.example.SistemaDeGestion.interfaces.ICajaLoginService;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import com.example.SistemaDeGestion.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class CajaLoginService implements ICajaLoginService {

    private final CajaRepository cajaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CajaLoginResDto execute(CajaLoginReqDto request) {
        Caja caja = cajaRepository.findById(request.idCaja())
                .orElseThrow(() -> new NotFoundException("Caja no encontrada con id " + request.idCaja()));

        if (!passwordEncoder.matches(request.password(), caja.getPasswordHash())) {
            throw new BadRequestException("Contraseña incorrecta");
        }

        if (caja.getEstado() == EstadoCaja.NO_DISPONIBLE) {
            throw new BadRequestException("La caja no está disponible (sin fondos)");
        }

        // Abrir la caja: cambiar a ACTIVA, registrar quien la abrió
        caja.setEstado(EstadoCaja.ACTIVA);
        // El usuario actual se obtendría del contexto de seguridad; por simplicidad usamos el ID
        // En producción se obtendría del SecurityContextHolder
        caja.setAbiertaPor(1L); // TODO: obtener del contexto real
        caja.setFechaApertura(Instant.now());
        caja.setFechaActualizacion(Instant.now());
        cajaRepository.save(caja);

        String token = jwtUtil.generateToken(
                "caja_" + caja.getIdCaja(),
                "CAJA",
                java.util.Map.of("idCaja", caja.getIdCaja(), "nombre", caja.getNombre())
        );

        return new CajaLoginResDto(
                caja.getIdCaja(),
                caja.getNombre(),
                caja.getMontoActual(),
                token
        );
    }
}
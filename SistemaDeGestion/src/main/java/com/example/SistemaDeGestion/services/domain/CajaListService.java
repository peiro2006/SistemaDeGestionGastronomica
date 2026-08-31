package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.interfaces.ICajaListService;
import com.example.SistemaDeGestion.mappers.CajaMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.models.EstadoCaja;
import com.example.SistemaDeGestion.repositories.CajaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CajaListService implements ICajaListService {

    private final CajaRepository cajaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CajaResDto> listarTodas() {
        return CajaMapper.toResponseDtoList(cajaRepository.findAllByOrderByFechaCreacionDesc());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaResDto> listarDisponibles() {
        return CajaMapper.toResponseDtoList(
                cajaRepository.findByEstadoIn(List.of(EstadoCaja.INACTIVA, EstadoCaja.ACTIVA))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResDto obtenerPorId(Long idCaja) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new NotFoundException("Caja no encontrada con id " + idCaja));
        return CajaMapper.toResponseDto(caja);
    }
}
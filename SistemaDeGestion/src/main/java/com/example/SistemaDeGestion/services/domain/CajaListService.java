package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.mappers.CajaMapper;
import com.example.SistemaDeGestion.repositories.CajasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CajaListService {

    private final CajasRepository cajasRepository;

    @Transactional(readOnly = true)
    public List<CajaResDto> execute() {
        return CajaMapper.toResponseDtoList(cajasRepository.findAllByOrderByFechaCreacionDesc());
    }

}
package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.interfaces.ICreateCajaService;
import com.example.SistemaDeGestion.mappers.CajaMapper;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.repositories.CajasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CajaCreateService implements ICreateCajaService {

    private final CajasRepository cajasRepository;

    @Override
    @Transactional
    public CajaResDto execute(CajaCreateReqDto request) {
        if (cajasRepository.existsByNombreCajaIgnoreCase(request.nombreCaja())) {
            throw new ConflictException("Ya existe una caja registrada con el nombre " + request.nombreCaja());
        }

        Caja caja = CajaMapper.toModel(request);
        return CajaMapper.toResponseDto(cajasRepository.save(caja));
    }

}
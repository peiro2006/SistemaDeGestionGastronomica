package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.InsumoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.InsumoResDto;
import com.example.SistemaDeGestion.mappers.InsumoMapper;
import com.example.SistemaDeGestion.models.Insumo;
import com.example.SistemaDeGestion.repositories.InsumosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class InsumoService {

    private final InsumosRepository insumosRepository;

    @Transactional(readOnly = true)
    public List<InsumoResDto> listar() {
        return InsumoMapper.toResponseDtoList(insumosRepository.findAll());
    }

    @Transactional
    public InsumoResDto crear(InsumoCreateReqDto request) {
        if (insumosRepository.existsByNombreInsumoIgnoreCase(request.nombreInsumo())) {
            throw new ConflictException("Ya existe un insumo registrado con el nombre " + request.nombreInsumo());
        }
        Insumo insumo = InsumoMapper.toModel(request);
        return InsumoMapper.toResponseDto(insumosRepository.save(insumo));
    }

}

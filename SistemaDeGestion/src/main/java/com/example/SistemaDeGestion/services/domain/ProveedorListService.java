package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.mappers.ProveedorMapper;
import com.example.SistemaDeGestion.repositories.ProveedoresRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProveedorListService {

    private final ProveedoresRepository proveedoresRepository;

    @Transactional(readOnly = true)
    public List<ProveedorResDto> execute() {
        return ProveedorMapper.toResponseDtoList(proveedoresRepository.findAllByOrderByFechaCreacionDesc());
    }

}
package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.interfaces.IProveedorListService;
import com.example.SistemaDeGestion.mappers.ProveedorMapper;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.repositories.ProveedorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProveedorListService implements IProveedorListService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResDto> listarTodas() {
        return ProveedorMapper.toResponseDtoList(proveedorRepository.findAllByOrderByNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResDto> listarActivos() {
        return ProveedorMapper.toResponseDtoList(proveedorRepository.findByActivoTrueOrderByNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResDto obtenerPorId(Long idProveedor) {
        return ProveedorMapper.toResponseDto(
                proveedorRepository.findById(idProveedor)
                        .orElseThrow(() -> new NotFoundException("Proveedor no encontrada con id " + idProveedor))
        );
    }
}

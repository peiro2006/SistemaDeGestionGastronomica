package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.interfaces.IProveedorCreateService;
import com.example.SistemaDeGestion.mappers.ProveedorMapper;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.repositories.ProveedorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProveedorCreateService implements IProveedorCreateService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public ProveedorResDto execute(ProveedorCreateReqDto request) {
        if (proveedorRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new BadRequestException("Ya existe un proveedor con ese nombre");
        }
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(request.nombre());
        proveedor.setTelefono(request.telefono());
        proveedor.setEmail(request.email());
        proveedor.setDireccion(request.direccion());
        proveedor.setCiudad(request.ciudad());
        proveedor.setActivo(true);
        return ProveedorMapper.toResponseDto(proveedorRepository.save(proveedor));
    }
}
package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.interfaces.IProveedorUpdateService;
import com.example.SistemaDeGestion.mappers.ProveedorMapper;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.repositories.ProveedorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProveedorUpdateService implements IProveedorUpdateService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public ProveedorResDto execute(Long idProveedor, ProveedorUpdateReqDto request) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new NotFoundException("Proveedor no encontrada con id " + idProveedor));

        if (request.nombre() != null && !request.nombre().equals(proveedor.getNombre())) {
            if (proveedorRepository.existsByNombreIgnoreCase(request.nombre())) {
                throw new BadRequestException("Ya existe un proveedor con ese nombre");
            }
            proveedor.setNombre(request.nombre());
        }
        if (request.telefono() != null) proveedor.setTelefono(request.telefono());
        if (request.email() != null) proveedor.setEmail(request.email());
        if (request.direccion() != null) proveedor.setDireccion(request.direccion());
        if (request.ciudad() != null) proveedor.setCiudad(request.ciudad());
        if (request.activo() != null) proveedor.setActivo(request.activo());

        return ProveedorMapper.toResponseDto(proveedorRepository.save(proveedor));
    }
}

package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.models.Proveedor;

import java.util.List;

public class ProveedorMapper {

    public static ProveedorResDto toResponseDto(Proveedor proveedor) {
        if (proveedor == null) return null;
        return new ProveedorResDto(
                proveedor.getIdProveedor(),
                proveedor.getNombre(),
                proveedor.getTelefono(),
                proveedor.getEmail(),
                proveedor.getDireccion(),
                proveedor.getCiudad(),
                proveedor.isActivo(),
                proveedor.getFechaCreacion()
        );
    }

    public static List<ProveedorResDto> toResponseDtoList(List<Proveedor> proveedores) {
        if (proveedores == null) return List.of();
        return proveedores.stream().map(ProveedorMapper::toResponseDto).toList();
    }
}
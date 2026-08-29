package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.models.Proveedor;

import java.util.List;

public class ProveedorMapper {

    private ProveedorMapper() {
    }

    public static Proveedor toModel(ProveedorCreateReqDto request) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(request.razonSocial());
        proveedor.setCuitRut(request.cuitRut());
        proveedor.setTelefono(request.telefono());
        proveedor.setCorreo(request.correo());
        proveedor.setDireccion(request.direccion());
        return proveedor;
    }

    public static void updateModel(Proveedor proveedor, ProveedorUpdateReqDto request) {
        proveedor.setRazonSocial(request.razonSocial());
        proveedor.setCuitRut(request.cuitRut());
        proveedor.setTelefono(request.telefono());
        proveedor.setCorreo(request.correo());
        proveedor.setDireccion(request.direccion());
    }

    public static ProveedorResDto toResponseDto(Proveedor proveedor) {
        return new ProveedorResDto(
                proveedor.getIdProveedor(),
                proveedor.getRazonSocial(),
                proveedor.getCuitRut(),
                proveedor.getTelefono(),
                proveedor.getCorreo(),
                proveedor.getDireccion(),
                proveedor.getFechaCreacion(),
                proveedor.getFechaUltimaModificacion(),
                proveedor.getUsuarioAlta(),
                proveedor.getUsuarioUltimaModificacion()
        );
    }

    public static List<ProveedorResDto> toResponseDtoList(List<Proveedor> models) {
        return models.stream()
                .map(ProveedorMapper::toResponseDto)
                .toList();
    }

}
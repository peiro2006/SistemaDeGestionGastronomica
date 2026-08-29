package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.mappers.ProveedorMapper;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.ProveedoresRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProveedorUpdateService {

    private final ProveedoresRepository proveedoresRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ProveedorResDto execute(Long idProveedor, ProveedorUpdateReqDto request) {
        Proveedor proveedor = proveedoresRepository.findById(idProveedor)
                .orElseThrow(() -> new NotFoundException("No existe un proveedor con el id " + idProveedor));

        if (proveedoresRepository.existsByCuitRutAndIdProveedorNot(request.cuitRut(), idProveedor)) {
            throw new ConflictException("Ya existe un proveedor registrado con el CUIT/RUT " + request.cuitRut());
        }

        String usuarioEmail = obtenerUsuarioAutenticado();
        proveedor.setFechaUltimaModificacion(LocalDateTime.now());
        proveedor.setUsuarioUltimaModificacion(usuarioEmail);
        ProveedorMapper.updateModel(proveedor, request);
        return ProveedorMapper.toResponseDto(proveedoresRepository.save(proveedor));
    }

    private String obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("No se pudo identificar al usuario autenticado");
        }
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .map(Usuario::getEmail)
                .orElseThrow(() -> new NotFoundException("No existe el usuario autenticado"));
    }

}
package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.interfaces.ICreateProveedorService;
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

@Service
@AllArgsConstructor
public class ProveedorCreateService implements ICreateProveedorService {

    private final ProveedoresRepository proveedoresRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public ProveedorResDto execute(ProveedorCreateReqDto request) {
        if (proveedoresRepository.existsByCuitRut(request.cuitRut())) {
            throw new ConflictException("Ya existe un proveedor registrado con el CUIT/RUT " + request.cuitRut());
        }

        String usuarioEmail = obtenerUsuarioAutenticado();
        Proveedor proveedor = ProveedorMapper.toModel(request);
        proveedor.setUsuarioAlta(usuarioEmail);
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
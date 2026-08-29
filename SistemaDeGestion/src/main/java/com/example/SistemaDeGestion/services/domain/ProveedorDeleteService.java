package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.repositories.ProveedoresRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProveedorDeleteService {

    private final ProveedoresRepository proveedoresRepository;

    @Transactional
    public void execute(Long idProveedor) {
        Proveedor proveedor = proveedoresRepository.findById(idProveedor)
                .orElseThrow(() -> new NotFoundException("No existe un proveedor con el id " + idProveedor));
        proveedoresRepository.delete(proveedor);
    }

}
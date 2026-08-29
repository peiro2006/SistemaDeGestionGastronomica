package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.repositories.ProveedoresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProveedorDeleteServiceTest {

    private ProveedoresRepository proveedoresRepository;
    private ProveedorDeleteService service;

    @BeforeEach
    void setUp() {
        proveedoresRepository = mock(ProveedoresRepository.class);
        service = new ProveedorDeleteService(proveedoresRepository);
    }

    @Test
    void eliminaProveedorExistente() {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        when(proveedoresRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        service.execute(1L);

        verify(proveedoresRepository).delete(proveedor);
    }

    @Test
    void lanzaNotFoundCuandoNoExiste() {
        when(proveedoresRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.execute(1L));
        verify(proveedoresRepository, never()).delete(any(Proveedor.class));
    }
}
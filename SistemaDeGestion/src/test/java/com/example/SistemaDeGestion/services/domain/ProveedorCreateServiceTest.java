package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.ProveedorCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.ProveedoresRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProveedorCreateServiceTest {

    private ProveedoresRepository proveedoresRepository;
    private UsuarioRepository usuarioRepository;
    private ProveedorCreateService service;

    @BeforeEach
    void setUp() {
        proveedoresRepository = mock(ProveedoresRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new ProveedorCreateService(proveedoresRepository, usuarioRepository);

        Usuario admin = new Usuario();
        admin.setEmail("admin@hamburbesa.com");
        when(usuarioRepository.findByEmailIgnoreCase("admin@hamburbesa.com")).thenReturn(Optional.of(admin));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@hamburbesa.com",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void creaProveedorCorrectamente() {
        when(proveedoresRepository.existsByCuitRut("30111222333")).thenReturn(false);
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setRazonSocial("Distribuidora Central SA");
        proveedor.setCuitRut("30111222333");
        proveedor.setTelefono("+54 11 5555 4444");
        proveedor.setCorreo("ventas@distribuidoracentral.com");
        proveedor.setDireccion("Av. Corrientes 1234, CABA");
        when(proveedoresRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        ProveedorCreateReqDto request = new ProveedorCreateReqDto(
                "Distribuidora Central SA",
                "30111222333",
                "+54 11 5555 4444",
                "ventas@distribuidoracentral.com",
                "Av. Corrientes 1234, CABA"
        );

        ProveedorResDto result = service.execute(request);

        assertNotNull(result);
        assertEquals(1L, result.idProveedor());
        assertEquals("Distribuidora Central SA", result.razonSocial());
        assertEquals("30111222333", result.cuitRut());
        assertEquals("ventas@distribuidoracentral.com", result.correo());

        ArgumentCaptor<Proveedor> captor = ArgumentCaptor.forClass(Proveedor.class);
        verify(proveedoresRepository).save(captor.capture());
        assertEquals("30111222333", captor.getValue().getCuitRut());
        assertEquals("admin@hamburbesa.com", captor.getValue().getUsuarioAlta());
    }

    @Test
    void rechazaCuitRutDuplicado() {
        when(proveedoresRepository.existsByCuitRut("30111222333")).thenReturn(true);

        ProveedorCreateReqDto request = new ProveedorCreateReqDto(
                "Distribuidora Central SA",
                "30111222333",
                "+54 11 5555 4444",
                "ventas@distribuidoracentral.com",
                "Av. Corrientes 1234, CABA"
        );

        assertThrows(ConflictException.class, () -> service.execute(request));
        verify(proveedoresRepository, never()).save(any(Proveedor.class));
    }
}
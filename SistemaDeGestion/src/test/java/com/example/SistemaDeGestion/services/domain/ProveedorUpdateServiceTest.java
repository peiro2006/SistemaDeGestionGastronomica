package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.ProveedorUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProveedorResDto;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.ProveedoresRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProveedorUpdateServiceTest {

    private ProveedoresRepository proveedoresRepository;
    private UsuarioRepository usuarioRepository;
    private ProveedorUpdateService service;

    @BeforeEach
    void setUp() {
        proveedoresRepository = mock(ProveedoresRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new ProveedorUpdateService(proveedoresRepository, usuarioRepository);

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
    void actualizaProveedorCorrectamenteConAuditoria() {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setRazonSocial("Distribuidora Central SA");
        proveedor.setCuitRut("30111222333");
        proveedor.setTelefono("+54 11 5555 4444");
        proveedor.setCorreo("ventas@distribuidoracentral.com");
        proveedor.setDireccion("Av. Corrientes 1234, CABA");
        proveedor.setUsuarioAlta("admin@hamburbesa.com");
        when(proveedoresRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedoresRepository.existsByCuitRutAndIdProveedorNot("30111222334", 1L)).thenReturn(false);
        when(proveedoresRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        ProveedorUpdateReqDto request = new ProveedorUpdateReqDto(
                "Distribuidora Central Renovada SA",
                "30111222334",
                "+54 11 5555 9999",
                "contacto@distribuidoracentral.com",
                "Av. Elcano 500, MDP"
        );

        ProveedorResDto result = service.execute(1L, request);

        assertNotNull(result);
        assertEquals("Distribuidora Central Renovada SA", result.razonSocial());
        assertEquals("30111222334", result.cuitRut());
        assertEquals("contacto@distribuidoracentral.com", result.correo());
        assertNotNull(result.fechaUltimaModificacion());
        verify(proveedoresRepository).save(proveedor);
    }

    @Test
    void lanzaNotFoundCuandoNoExiste() {
        when(proveedoresRepository.findById(99L)).thenReturn(Optional.empty());

        ProveedorUpdateReqDto request = new ProveedorUpdateReqDto(
                "Distribuidora Central SA",
                "30111222333",
                "+54 11 5555 4444",
                "ventas@distribuidoracentral.com",
                "Av. Corrientes 1234, CABA"
        );

        assertThrows(NotFoundException.class, () -> service.execute(99L, request));
        verify(proveedoresRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void rechazaCuitRutDuplicadoEnOtroProveedor() {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        when(proveedoresRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedoresRepository.existsByCuitRutAndIdProveedorNot("30111222333", 1L)).thenReturn(true);

        ProveedorUpdateReqDto request = new ProveedorUpdateReqDto(
                "Distribuidora Central SA",
                "30111222333",
                "+54 11 5555 4444",
                "ventas@distribuidoracentral.com",
                "Av. Corrientes 1234, CABA"
        );

        assertThrows(ConflictException.class, () -> service.execute(1L, request));
        verify(proveedoresRepository, never()).save(any(Proveedor.class));
    }
}
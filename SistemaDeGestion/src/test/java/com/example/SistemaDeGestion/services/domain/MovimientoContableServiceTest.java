package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.MovimientoContableCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.MovimientoContableResDto;
import com.example.SistemaDeGestion.dtos.response.MovimientoPageResDto;
import com.example.SistemaDeGestion.models.MovimientoContable;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.MovimientoContableRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MovimientoContableServiceTest {

    private MovimientoContableRepository repository;
    private UsuarioRepository usuarioRepository;
    private MovimientoContableService service;

    @BeforeEach
    void setUp() {
        repository = mock(MovimientoContableRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new MovimientoContableService(repository, usuarioRepository);

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
    void listarDevuelvePaginaConTotales() {
        MovimientoContable ingreso = new MovimientoContable();
        ingreso.setIdMovimiento(1L);
        ingreso.setTipo("INGRESO");
        ingreso.setMonto(new BigDecimal("1000"));
        ingreso.setMetodoPago("EFECTIVO");
        ingreso.setFecha(Instant.now());

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ingreso)));
        when(repository.sumarMontoEntre(eq("INGRESO"), any(), any())).thenReturn(new BigDecimal("1000"));
        when(repository.sumarMontoEntre(eq("EGRESO"), any(), any())).thenReturn(new BigDecimal("200"));

        MovimientoPageResDto page = service.listar(null, null, null, null, null, null, null, 0, 20);

        assertEquals(1, page.content().size());
        assertEquals(new BigDecimal("1000"), page.totalIngresos());
        assertEquals(new BigDecimal("200"), page.totalEgresos());
        assertEquals(new BigDecimal("800"), page.balance());
        assertEquals("EFECTIVO", page.content().get(0).metodoPago());
    }

    @Test
    void registraIngresoConMetodoDePago() {
        MovimientoContable guardado = new MovimientoContable();
        guardado.setIdMovimiento(1L);
        guardado.setTipo("INGRESO");
        guardado.setMonto(new BigDecimal("1500"));
        guardado.setMetodoPago("DEBITO");
        when(repository.save(any(MovimientoContable.class))).thenReturn(guardado);

        MovimientoContableResDto result = service.registrar(
                new MovimientoContableCreateReqDto("INGRESO", new BigDecimal("1500"), "Venta local", "DEBITO", null)
        );

        assertEquals("INGRESO", result.tipo());
        assertEquals("DEBITO", result.metodoPago());
        assertEquals(new BigDecimal("1500"), result.monto());

        verify(repository).save(argThat(m -> "DEBITO".equals(m.getMetodoPago())
                && "admin@hamburbesa.com".equals(m.getRegistradoPor())));
    }

    @Test
    void registraEgresoSinMetodoDePago() {
        MovimientoContable guardado = new MovimientoContable();
        guardado.setIdMovimiento(2L);
        guardado.setTipo("EGRESO");
        guardado.setMonto(new BigDecimal("500"));
        when(repository.save(any(MovimientoContable.class))).thenReturn(guardado);

        MovimientoContableResDto result = service.registrar(
                new MovimientoContableCreateReqDto("EGRESO", new BigDecimal("500"), "Pago a proveedor", null, null)
        );

        assertEquals("EGRESO", result.tipo());
        assertNull(result.metodoPago());
        verify(repository).save(argThat(m -> "EGRESO".equals(m.getTipo()) && m.getMetodoPago() == null));
    }

    @Test
    void rechazaIngresoSinMetodoDePago() {
        assertThrows(BadRequestException.class, () -> service.registrar(
                new MovimientoContableCreateReqDto("INGRESO", new BigDecimal("100"), "Venta", null, null)
        ));
    }

    @Test
    void rechazaTipoInvalido() {
        assertThrows(BadRequestException.class, () -> service.registrar(
                new MovimientoContableCreateReqDto("OTRO", new BigDecimal("100"), "X", "EFECTIVO", null)
        ));
    }

    @Test
    void obtenerPorIdLanzaNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.obtenerPorId(99L));
    }
}

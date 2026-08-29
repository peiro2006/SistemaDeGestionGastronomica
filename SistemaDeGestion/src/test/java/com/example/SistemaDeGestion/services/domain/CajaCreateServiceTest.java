package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.CajaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.CajaResDto;
import com.example.SistemaDeGestion.models.Caja;
import com.example.SistemaDeGestion.repositories.CajasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CajaCreateServiceTest {

    private CajasRepository cajasRepository;
    private CajaCreateService service;

    @BeforeEach
    void setUp() {
        cajasRepository = mock(CajasRepository.class);
        service = new CajaCreateService(cajasRepository);
    }

    @Test
    void creaCajaCorrectamente() {
        when(cajasRepository.existsByNombreCajaIgnoreCase("Caja Central")).thenReturn(false);
        Caja caja = new Caja();
        caja.setIdCaja(1L);
        caja.setNombreCaja("Caja Central");
        caja.setMontoInicial(BigDecimal.ZERO);
        caja.setMoneda("ARS");
        caja.setActiva(true);
        when(cajasRepository.save(any(Caja.class))).thenReturn(caja);

        CajaCreateReqDto request = new CajaCreateReqDto("Caja Central", BigDecimal.ZERO, "ars", "Caja principal", true);

        CajaResDto result = service.execute(request);

        assertNotNull(result);
        assertEquals(1L, result.idCaja());
        assertEquals("Caja Central", result.nombreCaja());
        assertEquals("ARS", result.moneda());
        assertTrue(result.activa());

        ArgumentCaptor<Caja> captor = ArgumentCaptor.forClass(Caja.class);
        verify(cajasRepository).save(captor.capture());
        assertEquals("ARS", captor.getValue().getMoneda());
    }

    @Test
    void rechazaNombreDuplicado() {
        when(cajasRepository.existsByNombreCajaIgnoreCase("Caja Central")).thenReturn(true);

        CajaCreateReqDto request = new CajaCreateReqDto("Caja Central", BigDecimal.ZERO, "ARS", "Caja principal", true);

        assertThrows(ConflictException.class, () -> service.execute(request));
        verify(cajasRepository, never()).save(any(Caja.class));
    }

    @Test
    void activaPorDefectoCuandoEsNull() {
        when(cajasRepository.existsByNombreCajaIgnoreCase("Caja Central")).thenReturn(false);
        when(cajasRepository.save(any(Caja.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CajaCreateReqDto request = new CajaCreateReqDto("Caja Central", BigDecimal.ZERO, "ARS", null, null);

        CajaResDto result = service.execute(request);

        assertTrue(result.activa());
        assertTrue(result.montoInicial().compareTo(BigDecimal.ZERO) == 0);
    }
}
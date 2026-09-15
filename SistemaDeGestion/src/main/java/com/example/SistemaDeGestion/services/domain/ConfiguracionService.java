package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.ConfiguracionResDto;
import com.example.SistemaDeGestion.mappers.ConfiguracionMapper;
import com.example.SistemaDeGestion.models.Configuracion;
import com.example.SistemaDeGestion.repositories.ConfiguracionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class ConfiguracionService {

    private static final String CLAVE_META_MENSUAL = "META_MENSUAL";
    private static final BigDecimal DEFAULT_META_MENSUAL = new BigDecimal("100000.00");

    private final ConfiguracionRepository configuracionRepository;

    @Transactional
    public ConfiguracionResDto obtener(String clave) {
        Configuracion config = configuracionRepository.findByClaveIgnoreCase(clave)
                .orElseGet(() -> crearDefault(clave));
        return ConfiguracionMapper.toResponseDto(config);
    }

    @Transactional
    public ConfiguracionResDto actualizar(String clave, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El valor debe ser un numero positivo");
        }
        Configuracion config = configuracionRepository.findByClaveIgnoreCase(clave)
                .orElseGet(() -> crearDefault(clave));
        config.setValor(valor);
        return ConfiguracionMapper.toResponseDto(configuracionRepository.save(config));
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerMetaMensual() {
        return configuracionRepository.findByClaveIgnoreCase(CLAVE_META_MENSUAL)
                .map(Configuracion::getValor)
                .orElse(DEFAULT_META_MENSUAL);
    }

    private Configuracion crearDefault(String clave) {
        Configuracion config = new Configuracion();
        config.setClave(clave);
        config.setValor(CLAVE_META_MENSUAL.equalsIgnoreCase(clave) ? DEFAULT_META_MENSUAL : BigDecimal.ZERO);
        return configuracionRepository.save(config);
    }
}

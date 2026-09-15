package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.response.ConfiguracionResDto;
import com.example.SistemaDeGestion.models.Configuracion;

public class ConfiguracionMapper {

    private ConfiguracionMapper() {
    }

    public static ConfiguracionResDto toResponseDto(Configuracion configuracion) {
        return new ConfiguracionResDto(
                configuracion.getId(),
                configuracion.getClave(),
                configuracion.getValor()
        );
    }
}

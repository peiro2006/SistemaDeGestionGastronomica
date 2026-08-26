package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.interfaces.IRecetaListService;
import com.example.SistemaDeGestion.mappers.RecetaMapper;
import com.example.SistemaDeGestion.models.Receta;
import com.example.SistemaDeGestion.repositories.RecetasRepository;
import com.example.SistemaDeGestion.repositories.specs.RecetaSpecs;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RecetaListService implements IRecetaListService {

    private final RecetasRepository recetasRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RecetaCreateResDto> execute(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return RecetaMapper.toResponseDtoList(recetasRepository.findAll());
        }
        return RecetaMapper.toResponseDtoList(
                recetasRepository.findAll(RecetaSpecs.byNombre(nombre))
        );
    }

}

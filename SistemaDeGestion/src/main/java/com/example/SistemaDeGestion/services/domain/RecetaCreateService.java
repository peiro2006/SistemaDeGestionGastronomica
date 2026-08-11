package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.interfaces.IRecetaCreateService;
import com.example.SistemaDeGestion.mappers.RecetaMapper;
import com.example.SistemaDeGestion.models.Receta;
import com.example.SistemaDeGestion.repositories.RecetasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecetaCreateService implements IRecetaCreateService {

    private final RecetasRepository recetasRepository;

    @Override
    public RecetaCreateResDto execute(RecetaCreateReqDto request) {
        if (recetasRepository.existsByNombreRecetaIgnoreCase(request.nombreReceta())) {
            throw new ConflictException("Ya existe una receta registrada con el nombre " + request.nombreReceta());
        }

        Receta receta = RecetaMapper.toModel(request);
        return RecetaMapper.toResponseDto(recetasRepository.save(receta));
    }

}

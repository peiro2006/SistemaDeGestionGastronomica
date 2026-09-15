package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.RecetaIngredienteReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.interfaces.IRecetaUpdateService;
import com.example.SistemaDeGestion.mappers.RecetaMapper;
import com.example.SistemaDeGestion.models.Insumo;
import com.example.SistemaDeGestion.models.Receta;
import com.example.SistemaDeGestion.models.RecetaInsumo;
import com.example.SistemaDeGestion.repositories.InsumosRepository;
import com.example.SistemaDeGestion.repositories.RecetasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RecetaUpdateService implements IRecetaUpdateService {

    private final RecetasRepository recetasRepository;
    private final InsumosRepository insumosRepository;

    @Override
    @Transactional
    public RecetaCreateResDto execute(Long idReceta, RecetaCreateReqDto request) {
        Receta receta = recetasRepository.findByIdConIngredientes(idReceta)
                .orElseThrow(() -> new NotFoundException("No existe una receta con el id " + idReceta));

        if (recetasRepository.existsByNombreRecetaIgnoreCase(request.nombreReceta())
                && !receta.getNombreReceta().equalsIgnoreCase(request.nombreReceta())) {
            throw new ConflictException("Ya existe otra receta registrada con el nombre " + request.nombreReceta());
        }

        if (request.ingredientes() == null || request.ingredientes().isEmpty()) {
            throw new BadRequestException("Debe ingresar al menos un ingrediente para la receta");
        }

        receta.setNombreReceta(request.nombreReceta());
        receta.setDescripcionReceta(request.descripcionReceta());
        receta.setIngredientesReceta(resumenIngredientes(request));

        receta.getIngredientes().clear();

        for (RecetaIngredienteReqDto ingredienteRequest : request.ingredientes()) {
            Insumo insumo = insumosRepository.findById(ingredienteRequest.idInsumo())
                    .orElseThrow(() -> new BadRequestException("No existe un insumo con el id " + ingredienteRequest.idInsumo()));

            RecetaInsumo ri = new RecetaInsumo();
            ri.setReceta(receta);
            ri.setInsumo(insumo);
            ri.setCantidad(ingredienteRequest.cantidad());
            receta.getIngredientes().add(ri);
        }

        return RecetaMapper.toResponseDto(recetasRepository.save(receta));
    }

    private String resumenIngredientes(RecetaCreateReqDto request) {
        if (request.ingredientes() == null || request.ingredientes().isEmpty()) {
            return request.ingredientesReceta();
        }
        return request.ingredientes().stream()
                .map(ingrediente -> ingrediente.cantidad() + " " + ingrediente.unidadMedida() + " " + ingrediente.nombreInsumo())
                .toList()
                .toString();
    }
}

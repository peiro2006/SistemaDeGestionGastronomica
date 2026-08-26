package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.dtos.response.RecetaIngredienteResDto;
import com.example.SistemaDeGestion.models.RecetaInsumo;
import com.example.SistemaDeGestion.models.Receta;

import java.util.Collections;
import java.util.List;

public class RecetaMapper {

    private RecetaMapper() {
    }

    public static Receta toModel(RecetaCreateReqDto request) {
        return Receta.builder()
                .nombreReceta(request.nombreReceta())
                .descripcionReceta(request.descripcionReceta())
                .ingredientesReceta(request.ingredientesReceta())
                .build();
    }

    public static RecetaCreateResDto toResponseDto(Receta receta) {
        return new RecetaCreateResDto(
                receta.getIdReceta(),
                receta.getNombreReceta(),
                receta.getDescripcionReceta(),
                receta.getIngredientesReceta(),
                toIngredienteResponseList(receta.getIngredientes())
        );
    }

    public static List<RecetaCreateResDto> toResponseDtoList(List<Receta> models) {
        return models.stream()
                .map(RecetaMapper::toResponseDto)
                .toList();
    }

    private static List<RecetaIngredienteResDto> toIngredienteResponseList(List<RecetaInsumo> ingredientes) {
        if (ingredientes == null) {
            return Collections.emptyList();
        }
        return ingredientes.stream()
                .map(ingrediente -> new RecetaIngredienteResDto(
                        ingrediente.getIdRecetaInsumo(),
                        ingrediente.getInsumo() != null ? ingrediente.getInsumo().getIdInsumo() : null,
                        ingrediente.getInsumo() != null ? ingrediente.getInsumo().getNombreInsumo() : null,
                        ingrediente.getInsumo() != null ? ingrediente.getInsumo().getUnidadMedida() : null,
                        ingrediente.getCantidad()
                ))
                .toList();
    }

}

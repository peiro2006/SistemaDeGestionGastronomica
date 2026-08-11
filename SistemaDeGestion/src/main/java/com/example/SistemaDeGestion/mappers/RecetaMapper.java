package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.models.Receta;

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
                receta.getIngredientesReceta()
        );
    }

    public static List<RecetaCreateResDto> toResponseDtoList(List<Receta> models) {
        return models.stream()
                .map(RecetaMapper::toResponseDto)
                .toList();
    }

}

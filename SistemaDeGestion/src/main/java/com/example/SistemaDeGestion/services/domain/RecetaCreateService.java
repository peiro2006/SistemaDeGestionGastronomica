package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.RecetaIngredienteReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.interfaces.IRecetaCreateService;
import com.example.SistemaDeGestion.mappers.RecetaMapper;
import com.example.SistemaDeGestion.models.Insumo;
import com.example.SistemaDeGestion.models.Receta;
import com.example.SistemaDeGestion.models.RecetaInsumo;
import com.example.SistemaDeGestion.repositories.InsumosRepository;
import com.example.SistemaDeGestion.repositories.RecetasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RecetaCreateService implements IRecetaCreateService {

    private final RecetasRepository recetasRepository;
    private final InsumosRepository insumosRepository;

    @Override
    @Transactional
    public RecetaCreateResDto execute(RecetaCreateReqDto request) {
        if (recetasRepository.existsByNombreRecetaIgnoreCase(request.nombreReceta())) {
            throw new ConflictException("Ya existe una receta registrada con el nombre " + request.nombreReceta());
        }

        if (isBlank(request.ingredientesReceta()) && ingredientesVacios(request.ingredientes())) {
            throw new BadRequestException("Debe ingresar al menos un ingrediente para la receta");
        }

        Receta receta = RecetaMapper.toModel(request);
        receta.setIngredientesReceta(resumenIngredientes(request));

        if (!ingredientesVacios(request.ingredientes())) {
            request.ingredientes().forEach(ingredienteRequest -> agregarIngrediente(receta, ingredienteRequest));
        }

        return RecetaMapper.toResponseDto(recetasRepository.save(receta));
    }

    private void agregarIngrediente(Receta receta, RecetaIngredienteReqDto request) {
        Insumo insumo = obtenerOCrearInsumo(request);
        receta.getIngredientes().add(
                RecetaInsumo.builder()
                        .receta(receta)
                        .insumo(insumo)
                        .cantidad(request.cantidad())
                        .build()
        );
    }

    private Insumo obtenerOCrearInsumo(RecetaIngredienteReqDto request) {
        if (request.idInsumo() != null) {
            return insumosRepository.findById(request.idInsumo())
                    .orElseThrow(() -> new BadRequestException("No existe un insumo con el id " + request.idInsumo()));
        }

        if (isBlank(request.nombreInsumo()) || isBlank(request.unidadMedida())) {
            throw new BadRequestException("Debe ingresar nombre y unidad de medida para cada insumo nuevo");
        }

        return insumosRepository.findByNombreInsumoIgnoreCase(request.nombreInsumo())
                .orElseGet(() -> insumosRepository.save(
                        Insumo.builder()
                                .nombreInsumo(request.nombreInsumo())
                                .unidadMedida(request.unidadMedida())
                                .stockActual(0)
                                .build()
                ));
    }

    private String resumenIngredientes(RecetaCreateReqDto request) {
        if (!isBlank(request.ingredientesReceta())) {
            return request.ingredientesReceta();
        }
        return request.ingredientes().stream()
                .map(ingrediente -> ingrediente.cantidad() + " " + ingrediente.unidadMedida() + " " + ingrediente.nombreInsumo())
                .toList()
                .toString();
    }

    private boolean ingredientesVacios(List<RecetaIngredienteReqDto> ingredientes) {
        return ingredientes == null || ingredientes.isEmpty();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}

package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.RecetaCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.RecetaCreateResDto;
import com.example.SistemaDeGestion.interfaces.IRecetaCreateService;
import com.example.SistemaDeGestion.interfaces.IRecetaListService;
import com.example.SistemaDeGestion.interfaces.IRecetaUpdateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Receta")
@AllArgsConstructor
public class RecetasController {

    private final IRecetaCreateService recetaCreateService;
    private final IRecetaListService recetaListService;
    private final IRecetaUpdateService recetaUpdateService;

    @PostMapping
    public ResponseEntity<BaseResponse<RecetaCreateResDto>> createReceta(
            @Valid @RequestBody RecetaCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        recetaCreateService.execute(request),
                        "Receta creada correctamente"
                )
        );
    }

    @PutMapping("/{idReceta}")
    public ResponseEntity<BaseResponse<RecetaCreateResDto>> updateReceta(
            @PathVariable Long idReceta,
            @Valid @RequestBody RecetaCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        recetaUpdateService.execute(idReceta, request),
                        "Receta actualizada correctamente"
                )
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<RecetaCreateResDto>>> listRecetas(
            @RequestParam(required = false) String nombre
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        recetaListService.execute(nombre),
                        "Recetas obtenidas correctamente"
                )
        );
    }

}

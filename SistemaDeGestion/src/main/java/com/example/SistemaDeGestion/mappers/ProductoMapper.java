package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.ProductoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Receta;

import java.util.List;

public class ProductoMapper {

    private ProductoMapper() {
    }

    public static Producto toModel(ProductoCreateReqDto request, Receta receta) {
        return Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .receta(receta)
                .build();
    }

    public static ProductoCreateResDto toResponseDto(Producto producto) {
        return new ProductoCreateResDto(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getReceta() != null ? producto.getReceta().getIdReceta() : null
        );
    }

    public static List<ProductoCreateResDto> toResponseDtoList(List<Producto> models) {
        return models.stream()
                .map(ProductoMapper::toResponseDto)
                .toList();
    }

}

package com.example.SistemaDeGestion.mappers;

import com.example.SistemaDeGestion.dtos.request.ProductoCreateReqDto;
import com.example.SistemaDeGestion.dtos.request.ProductoUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Receta;

import java.util.List;

public class ProductoMapper {

    private ProductoMapper() {
    }

    public static Producto toModel(ProductoCreateReqDto request, Receta receta) {
        Producto producto = new Producto();
        producto.setNombreProducto(request.nombreProducto());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setCategoria(request.categoria());
        producto.setImagenUrl(request.imagenUrl());
        producto.setActivo(true);
        producto.setStockActual(request.stockActual());
        producto.setStockMinimo(request.stockMinimo());
        producto.setReceta(receta);
        return producto;
    }

    public static void updateModel(Producto producto, ProductoUpdateReqDto request, Receta receta) {
        producto.setNombreProducto(request.nombreProducto());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setCategoria(request.categoria());
        producto.setImagenUrl(request.imagenUrl());
        producto.setStockMinimo(request.stockMinimo());
        producto.setReceta(receta);
    }

    public static ProductoCreateResDto toResponseDto(Producto producto) {
        return new ProductoCreateResDto(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria(),
                producto.getImagenUrl(),
                producto.getActivo(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getReceta() != null ? producto.getReceta().getIdReceta() : null,
                producto.getReceta() != null ? producto.getReceta().getNombreReceta() : null
        );
    }

    public static List<ProductoCreateResDto> toResponseDtoList(List<Producto> models) {
        return models.stream()
                .map(ProductoMapper::toResponseDto)
                .toList();
    }

}
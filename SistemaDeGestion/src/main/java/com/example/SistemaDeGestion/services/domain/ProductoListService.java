package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.interfaces.IProductoListService;
import com.example.SistemaDeGestion.mappers.ProductoMapper;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.specs.ProductoSpecs;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductoListService implements IProductoListService {

    private final ProductosRepository productosRepository;

    @Override
    public List<ProductoCreateResDto> execute(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return ProductoMapper.toResponseDtoList(productosRepository.findAll());
        }
        return ProductoMapper.toResponseDtoList(
                productosRepository.findAll(ProductoSpecs.byNombre(nombre))
        );
    }

}

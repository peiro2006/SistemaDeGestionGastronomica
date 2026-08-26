package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.mappers.ProductoMapper;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductoGetService {

    private final ProductosRepository productosRepository;

    @Transactional(readOnly = true)
    public ProductoCreateResDto execute(Long idProducto) {
        return productosRepository.findById(idProducto)
                .map(ProductoMapper::toResponseDto)
                .orElseThrow(() -> new NotFoundException("No existe un producto con el id " + idProducto));
    }

}

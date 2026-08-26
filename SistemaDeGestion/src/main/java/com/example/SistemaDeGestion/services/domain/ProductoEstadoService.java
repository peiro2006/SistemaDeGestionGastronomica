package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.mappers.ProductoMapper;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductoEstadoService {

    private final ProductosRepository productosRepository;

    @Transactional
    public ProductoCreateResDto execute(Long idProducto, Boolean activo) {
        Producto producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new NotFoundException("No existe un producto con el id " + idProducto));
        producto.setActivo(activo);
        return ProductoMapper.toResponseDto(productosRepository.save(producto));
    }

}

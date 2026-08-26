package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.mappers.ProductoMapper;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.specs.ProductoSpecs;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CatalogoProductosService {

    private final ProductosRepository productosRepository;

    @Transactional(readOnly = true)
    public List<ProductoCreateResDto> execute(String nombre, String categoria) {
        Specification<Producto> specification = Specification
                .where(ProductoSpecs.activos())
                .and(ProductoSpecs.conStockDisponible());

        if (nombre != null && !nombre.isBlank()) {
            specification = specification.and(ProductoSpecs.byNombre(nombre));
        }
        if (categoria != null && !categoria.isBlank()) {
            specification = specification.and(ProductoSpecs.byCategoria(categoria));
        }

        return ProductoMapper.toResponseDtoList(productosRepository.findAll(specification));
    }

}

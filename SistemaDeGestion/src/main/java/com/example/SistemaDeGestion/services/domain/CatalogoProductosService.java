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
import java.util.Map;

@Service
@AllArgsConstructor
public class CatalogoProductosService {

    private final ProductosRepository productosRepository;
    private final ProductoStockMaximoService productoStockMaximoService;

    @Transactional(readOnly = true)
    public List<ProductoCreateResDto> execute(String nombre, String categoria) {
        Specification<Producto> specification = Specification
                .where(ProductoSpecs.activos());

        if (nombre != null && !nombre.isBlank()) {
            specification = specification.and(ProductoSpecs.byNombre(nombre));
        }
        if (categoria != null && !categoria.isBlank()) {
            specification = specification.and(ProductoSpecs.byCategoria(categoria));
        }

        List<Producto> productos = productosRepository.findAll(specification);
        Map<Long, Integer> maximos = productoStockMaximoService.calcularStockMaximoTodos();
        return ProductoMapper.toResponseDtoList(productos, maximos);
    }

}

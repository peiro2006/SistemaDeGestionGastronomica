package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.ConflictException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.ProductoUpdateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;
import com.example.SistemaDeGestion.mappers.ProductoMapper;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Receta;
import com.example.SistemaDeGestion.models.Proveedor;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.RecetasRepository;
import com.example.SistemaDeGestion.repositories.ProveedorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductoUpdateService {

    private final ProductosRepository productosRepository;
    private final RecetasRepository recetasRepository;
    private final ProveedorRepository proveedorRepository;

    @Transactional
    public ProductoCreateResDto execute(Long idProducto, ProductoUpdateReqDto request) {
        Producto producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new NotFoundException("No existe un producto con el id " + idProducto));

        if (productosRepository.existsByNombreProductoIgnoreCaseAndIdProductoNot(request.nombreProducto(), idProducto)) {
            throw new ConflictException("Ya existe un producto registrado con el nombre " + request.nombreProducto());
        }

        Receta receta = recetasRepository.findById(request.idReceta())
                .orElseThrow(() -> new NotFoundException("No existe una receta registrada con el id " + request.idReceta()));

        ProductoMapper.updateModel(producto, request, receta);
        if (request.idProveedor() != null) {
            Proveedor proveedor = proveedorRepository.findById(request.idProveedor())
                    .orElseThrow(() -> new NotFoundException("No existe un proveedor con el id " + request.idProveedor()));
            producto.setProveedor(proveedor);
        }
        return ProductoMapper.toResponseDto(productosRepository.save(producto));
    }

}

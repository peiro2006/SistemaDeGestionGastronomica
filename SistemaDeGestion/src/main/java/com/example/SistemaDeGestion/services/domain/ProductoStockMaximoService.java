package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.models.Producto;
import com.example.SistemaDeGestion.models.Receta;
import com.example.SistemaDeGestion.models.RecetaInsumo;
import com.example.SistemaDeGestion.repositories.ProductosRepository;
import com.example.SistemaDeGestion.repositories.RecetasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProductoStockMaximoService {

    private final ProductosRepository productosRepository;
    private final RecetasRepository recetasRepository;

    @Transactional(readOnly = true)
    public int calcularStockMaximo(Long idProducto) {
        Producto producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new NotFoundException("No existe un producto con el id " + idProducto));

        return calcularStockMaximoDeReceta(producto.getReceta());
    }

    @Transactional(readOnly = true)
    public int calcularStockMaximoPorReceta(Long idReceta) {
        Receta receta = recetasRepository.findByIdConIngredientes(idReceta)
                .orElseThrow(() -> new NotFoundException("No existe una receta con el id " + idReceta));

        return calcularStockMaximoDeReceta(receta);
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> calcularStockMaximoTodos() {
        Map<Long, Integer> resultado = new HashMap<>();
        java.util.List<Producto> productos = productosRepository.findAllConRecetaEIngredientes();
        for (Producto producto : productos) {
            resultado.put(producto.getIdProducto(), calcularStockMaximoDeReceta(producto.getReceta()));
        }
        return resultado;
    }

    private int calcularStockMaximoDeReceta(Receta receta) {
        if (receta == null || receta.getIngredientes() == null || receta.getIngredientes().isEmpty()) {
            return 0;
        }

        int stockMaximo = Integer.MAX_VALUE;
        for (RecetaInsumo ri : receta.getIngredientes()) {
            int stockInsumo = ri.getInsumo().getStockActual() != null ? ri.getInsumo().getStockActual() : 0;
            BigDecimal cantidadPorUnidad = ri.getCantidad();
            if (cantidadPorUnidad == null || cantidadPorUnidad.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            int producible = BigDecimal.valueOf(stockInsumo)
                    .divide(cantidadPorUnidad, 0, RoundingMode.FLOOR)
                    .intValue();
            stockMaximo = Math.min(stockMaximo, producible);
        }

        return stockMaximo == Integer.MAX_VALUE ? 0 : stockMaximo;
    }
}

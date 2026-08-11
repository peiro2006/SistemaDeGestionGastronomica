package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;

import java.util.List;

public interface IProductoListService {

    List<ProductoCreateResDto> execute(String nombre);

}

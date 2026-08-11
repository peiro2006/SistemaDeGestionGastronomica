package com.example.SistemaDeGestion.interfaces;

import com.example.SistemaDeGestion.dtos.request.ProductoCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.ProductoCreateResDto;

public interface ICreateProductoService {

    ProductoCreateResDto execute(ProductoCreateReqDto request);

}

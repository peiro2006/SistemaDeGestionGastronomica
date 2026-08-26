import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Producto, ProductoCreateRequest, ProductoUpdateRequest } from '../models/producto.models';

@Injectable({ providedIn: 'root' })
export class ProductosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(nombre?: string): Observable<BaseResponse<Producto[]>> {
    const params = this.buildParams({ nombre });
    return this.http.get<BaseResponse<Producto[]>>(`${this.apiUrl}/Producto`, { params });
  }

  obtener(idProducto: number): Observable<BaseResponse<Producto>> {
    return this.http.get<BaseResponse<Producto>>(`${this.apiUrl}/Producto/${idProducto}`);
  }

  crear(data: ProductoCreateRequest): Observable<BaseResponse<Producto>> {
    return this.http.post<BaseResponse<Producto>>(`${this.apiUrl}/Producto`, data);
  }

  actualizar(idProducto: number, data: ProductoUpdateRequest): Observable<BaseResponse<Producto>> {
    return this.http.put<BaseResponse<Producto>>(`${this.apiUrl}/Producto/${idProducto}`, data);
  }

  cambiarEstado(idProducto: number, activo: boolean): Observable<BaseResponse<Producto>> {
    return this.http.patch<BaseResponse<Producto>>(`${this.apiUrl}/Producto/${idProducto}/estado`, { activo });
  }

  catalogo(filtros?: { nombre?: string; categoria?: string }): Observable<BaseResponse<Producto[]>> {
    const params = this.buildParams(filtros ?? {});
    return this.http.get<BaseResponse<Producto[]>>(`${this.apiUrl}/catalogo/productos`, { params });
  }

  private buildParams(values: { nombre?: string; categoria?: string }): HttpParams {
    let params = new HttpParams();
    if (values.nombre) {
      params = params.set('nombre', values.nombre);
    }
    if (values.categoria) {
      params = params.set('categoria', values.categoria);
    }
    return params;
  }
}

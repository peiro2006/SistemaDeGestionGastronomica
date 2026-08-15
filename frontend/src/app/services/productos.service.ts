import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Producto, ProductoCreateRequest } from '../models/producto.models';

@Injectable({ providedIn: 'root' })
export class ProductosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(): Observable<BaseResponse<Producto[]>> {
    return this.http.get<BaseResponse<Producto[]>>(`${this.apiUrl}/Producto`);
  }

  crear(data: ProductoCreateRequest): Observable<BaseResponse<Producto>> {
    return this.http.post<BaseResponse<Producto>>(`${this.apiUrl}/Producto`, data);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Insumo, InsumoCreateRequest } from '../models/insumo.models';

@Injectable({ providedIn: 'root' })
export class InsumosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(): Observable<BaseResponse<Insumo[]>> {
    return this.http.get<BaseResponse<Insumo[]>>(`${this.apiUrl}/admin/insumos`);
  }

  crear(data: InsumoCreateRequest): Observable<BaseResponse<Insumo>> {
    return this.http.post<BaseResponse<Insumo>>(`${this.apiUrl}/admin/insumos`, data);
  }
}

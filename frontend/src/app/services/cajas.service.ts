import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Caja, CajaCreateRequest } from '../models/caja.models';

@Injectable({ providedIn: 'root' })
export class CajasService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(): Observable<BaseResponse<Caja[]>> {
    return this.http.get<BaseResponse<Caja[]>>(`${this.apiUrl}/Caja`);
  }

  crear(data: CajaCreateRequest): Observable<BaseResponse<Caja>> {
    return this.http.post<BaseResponse<Caja>>(`${this.apiUrl}/Caja`, data);
  }
}
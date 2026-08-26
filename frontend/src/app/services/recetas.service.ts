import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Receta, RecetaCreateRequest } from '../models/receta.models';

@Injectable({ providedIn: 'root' })
export class RecetasService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(nombre?: string): Observable<BaseResponse<Receta[]>> {
    let params = new HttpParams();
    if (nombre) {
      params = params.set('nombre', nombre);
    }
    return this.http.get<BaseResponse<Receta[]>>(`${this.apiUrl}/Receta`, { params });
  }

  crear(data: RecetaCreateRequest): Observable<BaseResponse<Receta>> {
    return this.http.post<BaseResponse<Receta>>(`${this.apiUrl}/Receta`, data);
  }
}

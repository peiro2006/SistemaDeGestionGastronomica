import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { ReporteRes } from '../models/reporte.models';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  reporte(desde?: number, hasta?: number): Observable<BaseResponse<ReporteRes>> {
    let params = new HttpParams();
    if (desde != null) {
      params = params.set('desde', String(desde));
    }
    if (hasta != null) {
      params = params.set('hasta', String(hasta));
    }
    return this.http.get<BaseResponse<ReporteRes>>(`${this.apiUrl}/Reporte/pedidos`, { params });
  }
}
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { MovimientoContable, MovimientoCreateRequest, MovimientoPageRes } from '../models/movimiento.models';

@Injectable({ providedIn: 'root' })
export class MovimientoContableService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(filtros: {
    tipo?: string;
    metodoPago?: string;
    desde?: number;
    hasta?: number;
    montoMin?: number;
    montoMax?: number;
    concepto?: string;
    page?: number;
    size?: number;
  }): Observable<BaseResponse<MovimientoPageRes>> {
    let params = new HttpParams();
    const set = (k: string, v: unknown) => {
      if (v != null && v !== '') {
        params = params.set(k, String(v));
      }
    };
    set('tipo', filtros.tipo);
    set('metodoPago', filtros.metodoPago);
    set('desde', filtros.desde);
    set('hasta', filtros.hasta);
    set('montoMin', filtros.montoMin);
    set('montoMax', filtros.montoMax);
    set('concepto', filtros.concepto);
    set('page', filtros.page ?? 0);
    set('size', filtros.size ?? 20);
    return this.http.get<BaseResponse<MovimientoPageRes>>(`${this.apiUrl}/contable/movimientos`, { params });
  }

  detalle(idMovimiento: number): Observable<BaseResponse<MovimientoContable>> {
    return this.http.get<BaseResponse<MovimientoContable>>(`${this.apiUrl}/contable/movimientos/${idMovimiento}`);
  }

  registrar(data: MovimientoCreateRequest): Observable<BaseResponse<MovimientoContable>> {
    return this.http.post<BaseResponse<MovimientoContable>>(`${this.apiUrl}/contable/movimientos`, data);
  }

  exportar(filtros: {
    tipo?: string;
    metodoPago?: string;
    desde?: number;
    hasta?: number;
    montoMin?: number;
    montoMax?: number;
    concepto?: string;
  }): Observable<Blob> {
    let params = new HttpParams();
    const set = (k: string, v: unknown) => {
      if (v != null && v !== '') {
        params = params.set(k, String(v));
      }
    };
    set('tipo', filtros.tipo);
    set('metodoPago', filtros.metodoPago);
    set('desde', filtros.desde);
    set('hasta', filtros.hasta);
    set('montoMin', filtros.montoMin);
    set('montoMax', filtros.montoMax);
    set('concepto', filtros.concepto);
    return this.http.get(`${this.apiUrl}/contable/movimientos/exportar`, { params, responseType: 'blob' });
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Caja, CajaCreateRequest, CajaUpdateRequest, CajaEstadoRequest, CajaLoginRequest, CajaLoginResponse } from '../models/caja.models';

@Injectable({ providedIn: 'root' })
export class CajaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  crear(data: CajaCreateRequest): Observable<BaseResponse<Caja>> {
    return this.http.post<BaseResponse<Caja>>(`${this.apiUrl}/caja`, data);
  }

  listarTodas(): Observable<BaseResponse<Caja[]>> {
    return this.http.get<BaseResponse<Caja[]>>(`${this.apiUrl}/caja`);
  }

  listarDisponibles(): Observable<BaseResponse<Caja[]>> {
    return this.http.get<BaseResponse<Caja[]>>(`${this.apiUrl}/caja/disponibles`);
  }

  obtener(idCaja: number): Observable<BaseResponse<Caja>> {
    return this.http.get<BaseResponse<Caja>>(`${this.apiUrl}/caja/${idCaja}`);
  }

  actualizar(idCaja: number, data: CajaUpdateRequest): Observable<BaseResponse<Caja>> {
    return this.http.put<BaseResponse<Caja>>(`${this.apiUrl}/caja/${idCaja}`, data);
  }

  cambiarEstado(idCaja: number, data: CajaEstadoRequest): Observable<BaseResponse<Caja>> {
    return this.http.put<BaseResponse<Caja>>(`${this.apiUrl}/caja/${idCaja}/estado`, data);
  }

  loginCaja(data: CajaLoginRequest): Observable<BaseResponse<CajaLoginResponse>> {
    return this.http.post<BaseResponse<CajaLoginResponse>>(`${this.apiUrl}/caja/login`, data);
  }

  etiquetaEstado(estado: string): string {
    const map: Record<string, string> = {
      INACTIVA: 'Inactiva',
      ACTIVA: 'Activa',
      NO_DISPONIBLE: 'No disponible'
    };
    return map[estado] ?? estado;
  }

  estadoColor(estado: string): string {
    const map: Record<string, string> = {
      INACTIVA: 'estado-inactiva',
      ACTIVA: 'estado-activa',
      NO_DISPONIBLE: 'estado-no-disponible'
    };
    return map[estado] ?? '';
  }

  formatearMonto(monto: number): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(monto);
  }
}
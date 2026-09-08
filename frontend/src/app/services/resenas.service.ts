import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';

export interface Resena {
  idResena: number;
  idPedido: number;
  idUsuario: number;
  nombreUsuario: string;
  nombreProducto: string | null;
  calificacion: number;
  comentario: string | null;
  fechaCreacion: string;
}

export interface ResenaCreateRequest {
  calificacion: number;
  comentario?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ResenasService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  crear(idPedido: number, data: ResenaCreateRequest): Observable<BaseResponse<Resena>> {
    return this.http.post<BaseResponse<Resena>>(`${this.apiUrl}/pedido/${idPedido}/resena`, data);
  }

  listarPorPedido(idPedido: number): Observable<BaseResponse<Resena[]>> {
    return this.http.get<BaseResponse<Resena[]>>(`${this.apiUrl}/pedido/${idPedido}/resena`);
  }

  listarTodas(): Observable<BaseResponse<Resena[]>> {
    return this.http.get<BaseResponse<Resena[]>>(`${this.apiUrl}/resena`);
  }

  listarConFiltros(filtros: { producto?: string; fecha?: string; usuario?: string }): Observable<BaseResponse<Resena[]>> {
    let params = new HttpParams();
    if (filtros.producto) params = params.set('producto', filtros.producto);
    if (filtros.fecha) params = params.set('fecha', filtros.fecha);
    if (filtros.usuario) params = params.set('usuario', filtros.usuario);
    return this.http.get<BaseResponse<Resena[]>>(`${this.apiUrl}/resena/filtro`, { params });
  }
}

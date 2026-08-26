import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';

export interface Resena {
  idResena: number;
  idPedido: number;
  idUsuario: number;
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
    return this.http.post<BaseResponse<Resena>>(`${this.apiUrl}/pedidos/${idPedido}/resena`, data);
  }

  listarPorPedido(idPedido: number): Observable<BaseResponse<Resena[]>> {
    return this.http.get<BaseResponse<Resena[]>>(`${this.apiUrl}/pedidos/${idPedido}/resena`);
  }
}

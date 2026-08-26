import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Pedido, PedidoCreateRequest } from '../models/pedido.models';

@Injectable({ providedIn: 'root' })
export class PedidosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  crear(data: PedidoCreateRequest): Observable<BaseResponse<Pedido>> {
    return this.http.post<BaseResponse<Pedido>>(`${this.apiUrl}/pedidos`, data);
  }

  listarMisPedidos(): Observable<BaseResponse<Pedido[]>> {
    return this.http.get<BaseResponse<Pedido[]>>(`${this.apiUrl}/pedidos`);
  }

  obtenerPorId(idPedido: number): Observable<BaseResponse<Pedido>> {
    return this.http.get<BaseResponse<Pedido>>(`${this.apiUrl}/pedidos/${idPedido}`);
  }

  cambiarEstado(idPedido: number, estado: string): Observable<BaseResponse<Pedido>> {
    return this.http.patch<BaseResponse<Pedido>>(`${this.apiUrl}/pedidos/${idPedido}/estado`, { estado });
  }
}

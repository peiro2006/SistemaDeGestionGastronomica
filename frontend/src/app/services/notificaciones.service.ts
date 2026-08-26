import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';

export interface Notificacion {
  idNotificacion: number;
  idProducto: number | null;
  nombreProducto: string | null;
  mensaje: string;
  leida: boolean;
  fecha: string;
}

@Injectable({ providedIn: 'root' })
export class NotificacionesService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(soloNoLeidas?: boolean): Observable<BaseResponse<Notificacion[]>> {
    let params = new HttpParams();
    if (soloNoLeidas !== undefined) {
      params = params.set('soloNoLeidas', soloNoLeidas);
    }
    return this.http.get<BaseResponse<Notificacion[]>>(`${this.apiUrl}/admin/notificaciones`, { params });
  }

  marcarLeida(idNotificacion: number): Observable<BaseResponse<Notificacion>> {
    return this.http.patch<BaseResponse<Notificacion>>(
      `${this.apiUrl}/admin/notificaciones/${idNotificacion}/leida`,
      {}
    );
  }
}

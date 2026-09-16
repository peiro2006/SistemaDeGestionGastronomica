import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse, Usuario } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class UsuariosAdminService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  buscarPorEmail(email: string): Observable<BaseResponse<Usuario>> {
    return this.http.get<BaseResponse<Usuario>>(`${this.apiUrl}/auth/usuarios/${encodeURIComponent(email)}`);
  }

  promover(email: string): Observable<BaseResponse<Usuario>> {
    return this.http.put<BaseResponse<Usuario>>(`${this.apiUrl}/auth/promover-empleado/${encodeURIComponent(email)}`, null);
  }

  desemplar(email: string): Observable<BaseResponse<Usuario>> {
    return this.http.put<BaseResponse<Usuario>>(`${this.apiUrl}/auth/desemplar-empleado/${encodeURIComponent(email)}`, null);
  }
}

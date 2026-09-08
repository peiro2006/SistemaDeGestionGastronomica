import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { Proveedor, ProveedorCreateRequest, ProveedorUpdateRequest } from '../models/proveedor.models';

@Injectable({ providedIn: 'root' })
export class ProveedoresService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listarTodas(): Observable<BaseResponse<Proveedor[]>> {
    return this.http.get<BaseResponse<Proveedor[]>>(`${this.apiUrl}/proveedor`);
  }

  listarActivos(): Observable<BaseResponse<Proveedor[]>> {
    return this.http.get<BaseResponse<Proveedor[]>>(`${this.apiUrl}/proveedor/activos`);
  }

  listarDisponibles(): Observable<BaseResponse<Proveedor[]>> {
    return this.http.get<BaseResponse<Proveedor[]>>(`${this.apiUrl}/proveedor/disponibles`);
  }

  obtenerPorId(idProveedor: number): Observable<BaseResponse<Proveedor>> {
    return this.http.get<BaseResponse<Proveedor>>(`${this.apiUrl}/proveedor/${idProveedor}`);
  }

  crear(data: ProveedorCreateRequest): Observable<BaseResponse<Proveedor>> {
    return this.http.post<BaseResponse<Proveedor>>(`${this.apiUrl}/proveedor`, data);
  }

  actualizar(idProveedor: number, data: ProveedorUpdateRequest): Observable<BaseResponse<Proveedor>> {
    return this.http.put<BaseResponse<Proveedor>>(`${this.apiUrl}/proveedor/${idProveedor}`, data);
  }
}
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseResponse } from '../models/auth.models';
import { StockAjusteRequest, StockMovimiento } from '../models/stock.models';

@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  listar(filtros?: { idProducto?: number; idInsumo?: number }): Observable<BaseResponse<StockMovimiento[]>> {
    let params = new HttpParams();
    if (filtros?.idProducto) {
      params = params.set('idProducto', filtros.idProducto);
    }
    if (filtros?.idInsumo) {
      params = params.set('idInsumo', filtros.idInsumo);
    }
    return this.http.get<BaseResponse<StockMovimiento[]>>(`${this.apiUrl}/admin/stock/movimientos`, { params });
  }

  ajustar(data: StockAjusteRequest): Observable<BaseResponse<StockMovimiento>> {
    return this.http.post<BaseResponse<StockMovimiento>>(`${this.apiUrl}/admin/stock/movimientos`, data);
  }
}

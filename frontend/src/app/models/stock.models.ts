export type TipoMovimientoStock = 'INGRESO' | 'EGRESO';

export interface StockAjusteRequest {
  idProducto?: number | null;
  idInsumo?: number | null;
  tipo: TipoMovimientoStock;
  cantidad: number;
  motivo: string;
}

export interface StockMovimiento {
  idStockMovimiento: number;
  idProducto: number | null;
  nombreProducto: string | null;
  idInsumo: number | null;
  nombreInsumo: string | null;
  tipo: TipoMovimientoStock;
  cantidad: number;
  motivo: string;
  saldoPosterior: number;
  idUsuario: number;
  usuarioEmail: string;
  fecha: string;
}

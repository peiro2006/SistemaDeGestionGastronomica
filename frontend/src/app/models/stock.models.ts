export type TipoMovimientoStock = 'INGRESO' | 'EGRESO';

export interface StockAjusteRequest {
  idProducto?: number | null;
  idInsumo?: number | null;
  idProveedor?: number | null;
  tipo: TipoMovimientoStock;
  cantidad: number;
  motivo: string;
  montoCompra?: number | null;
}

export interface StockMovimiento {
  idStockMovimiento: number;
  idProducto: number | null;
  nombreProducto: string | null;
  idInsumo: number | null;
  nombreInsumo: string | null;
  idProveedor: number | null;
  nombreProveedor: string | null;
  tipo: TipoMovimientoStock;
  cantidad: number;
  motivo: string;
  saldoPosterior: number;
  idUsuario: number;
  usuarioEmail: string;
  montoCompra: number | null;
  fecha: string;
}

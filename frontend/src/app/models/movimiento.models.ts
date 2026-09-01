export type TipoMovimiento = 'INGRESO' | 'EGRESO';
export type MetodoPago = 'EFECTIVO' | 'DEBITO' | 'TRANSFERENCIA';

export interface MovimientoContable {
  idMovimiento: number;
  tipo: TipoMovimiento;
  monto: number;
  concepto: string | null;
  metodoPago: MetodoPago | null;
  idCaja: number | null;
  fecha: string;
  registradoPor: string | null;
}

export interface MovimientoCreateRequest {
  tipo: TipoMovimiento;
  monto: number;
  concepto?: string | null;
  metodoPago?: MetodoPago | null;
  idCaja?: number | null;
}

export interface MovimientoPageRes {
  content: MovimientoContable[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
  totalIngresos: number;
  totalEgresos: number;
  balance: number;
}

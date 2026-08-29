export interface Caja {
  idCaja: number;
  nombreCaja: string;
  montoInicial: number;
  moneda: string;
  descripcionCaja: string | null;
  activa: boolean | null;
  fechaCreacion: string;
}

export interface CajaCreateRequest {
  nombreCaja: string;
  montoInicial: number;
  moneda: string;
  descripcionCaja?: string | null;
  activa: boolean;
}
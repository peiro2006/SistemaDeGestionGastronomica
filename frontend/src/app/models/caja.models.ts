export interface Caja {
  idCaja: number;
  nombre: string;
  descripcion: string | null;
  montoInicial: number;
  montoActual: number;
  estado: 'INACTIVA' | 'ACTIVA' | 'NO_DISPONIBLE';
  fechaCreacion: string;
  fechaActualizacion: string | null;
  abiertaPor: number | null;
  fechaApertura: string | null;
}

export interface CajaCreateRequest {
  nombre: string;
  descripcion?: string | null;
  moneda: string;
  montoInicial: number;
  password: string;
}

export interface CajaUpdateRequest {
  nombre?: string;
  descripcion?: string | null;
  montoInicial?: number;
  password?: string;
}

export interface CajaEstadoRequest {
  estado: 'INACTIVA' | 'ACTIVA' | 'NO_DISPONIBLE';
}

export interface CajaLoginRequest {
  idCaja: number;
  password: string;
}

export interface CajaLoginResponse {
  idCaja: number;
  nombre: string;
  montoActual: number;
  token: string;
}

export interface CajaResumen {
  totalEfectivo: number;
  totalDebito: number;
  totalCredito: number;
  totalTransferencia: number;
  totalNoEfectivo: number;
  montoActual: number;
}
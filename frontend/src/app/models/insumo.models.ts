export interface Insumo {
  idInsumo: number;
  nombreInsumo: string;
  unidadMedida: string;
  stockActual: number;
}

export interface InsumoCreateRequest {
  nombreInsumo: string;
  unidadMedida: string;
  stockActual: number;
}

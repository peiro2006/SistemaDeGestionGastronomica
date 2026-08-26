export interface RecetaIngrediente {
  idRecetaInsumo: number;
  idInsumo: number;
  nombreInsumo: string;
  unidadMedida: string;
  cantidad: number;
}

export interface Receta {
  idReceta: number;
  nombreReceta: string;
  descripcionReceta: string;
  ingredientesReceta: string;
  ingredientes: RecetaIngrediente[];
}

export interface RecetaIngredienteCreateRequest {
  idInsumo?: number | null;
  nombreInsumo?: string | null;
  unidadMedida?: string | null;
  cantidad: number;
}

export interface RecetaCreateRequest {
  nombreReceta: string;
  descripcionReceta: string;
  ingredientesReceta?: string | null;
  ingredientes?: RecetaIngredienteCreateRequest[];
}

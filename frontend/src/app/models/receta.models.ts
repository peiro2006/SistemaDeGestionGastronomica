export interface Receta {
  idReceta: number;
  nombreReceta: string;
  descripcionReceta: string;
  ingredientesReceta: string;
}

export interface RecetaCreateRequest {
  nombreReceta: string;
  descripcionReceta: string;
  ingredientesReceta: string;
}

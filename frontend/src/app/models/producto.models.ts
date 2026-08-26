export interface Producto {
  idProducto: number;
  nombreProducto: string;
  descripcion: string;
  precio: string;
  categoria: string | null;
  imagenUrl: string | null;
  activo: boolean | null;
  stockActual: number | null;
  stockMinimo: number | null;
  idReceta?: number;
  nombreReceta?: string | null;
}

export interface ProductoCreateRequest {
  nombreProducto: string;
  descripcion: string;
  precio: string;
  categoria: string;
  imagenUrl?: string | null;
  stockActual: number;
  stockMinimo: number;
  idReceta: number;
}

export interface ProductoUpdateRequest {
  nombreProducto: string;
  descripcion: string;
  precio: string;
  categoria: string;
  imagenUrl?: string | null;
  stockMinimo: number;
  idReceta: number;
}

export interface Producto {
  idProducto: number;
  nombreProducto: string;
  descripcion: string;
  precio: string;
  idReceta?: number;
}

export interface ProductoCreateRequest {
  nombreProducto: string;
  descripcion: string;
  precio: string;
  idReceta: number;
}

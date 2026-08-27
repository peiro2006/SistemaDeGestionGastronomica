export interface PedidoItemCreateRequest {
  idProducto: number;
  cantidad: number;
}

export interface PedidoCreateRequest {
  items: PedidoItemCreateRequest[];
}

export interface PedidoItem {
  idPedidoItem: number;
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Pedido {
  idPedido: number;
  idUsuario: number;
  estado: string;
  total: number;
  fechaCreacion: string;
  items: PedidoItem[];
}

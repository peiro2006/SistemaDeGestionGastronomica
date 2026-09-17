export type MetodoPago = 'EFECTIVO' | 'DEBITO' | 'TARJETA_CREDITO' | 'TRANSFERENCIA';

export interface PedidoItemCreateRequest {
  idProducto: number;
  cantidad: number;
}

export interface PedidoCreateRequest {
  items: PedidoItemCreateRequest[];
  metDePago: MetodoPago;
  indicaciones?: string;
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
  nombreUsuario: string;
  idCaja: number | null;
  estado: string;
  metDePago: string;
  total: number;
  fechaCreacion: string;
  items: PedidoItem[];
  indicaciones?: string;
}

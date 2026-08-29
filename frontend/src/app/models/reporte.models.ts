export type EstadoPedido = 'pendiente' | 'en_preparacion' | 'enviado' | 'entregado' | 'cancelado';

export interface ReporteRes {
  desde: string;
  hasta: string;
  totalFacturado: number;
  promedioValorPorPedido: number;
  cantidadPedidosCompletados: number;
  cantidadPedidosPeriodo: number;
  pedidosPorEstado: Record<EstadoPedido, number>;
}
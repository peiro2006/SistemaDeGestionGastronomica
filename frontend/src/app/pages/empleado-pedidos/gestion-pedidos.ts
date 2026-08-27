import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Pedido } from '../../models/pedido.models';
import { PedidosService } from '../../services/pedidos.service';

@Component({
  selector: 'app-empleado-pedidos',
  imports: [DatePipe],
  templateUrl: './gestion-pedidos.html',
  styleUrl: './gestion-pedidos.css'
})
export class EmpleadoPedidosComponent implements OnInit {
  private readonly pedidosService = inject(PedidosService);

  readonly pedidos = signal<Pedido[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly mensaje = signal<string | null>(null);

  readonly estados = ['pendiente', 'en_preparacion', 'enviado', 'entregado', 'cancelado'];
  readonly estadoCambio = signal<{ id: number; estado: string } | null>(null);
  readonly guardando = signal(false);

  readonly estadoActivos = ['pendiente', 'en_preparacion', 'enviado'];
  readonly pedidosActivos = computed(() =>
    this.pedidos().filter((p) => this.estadoActivos.includes(p.estado))
  );
  readonly pedidosFinalizados = computed(() =>
    this.pedidos().filter((p) => p.estado === 'entregado' || p.estado === 'cancelado')
  );

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.pedidosService.listarTodos().subscribe({
      next: (res) => {
        this.pedidos.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  etiquetaEstado(estado: string): string {
    const map: Record<string, string> = {
      pendiente: 'Pendiente',
      en_preparacion: 'En preparación',
      enviado: 'Enviado',
      entregado: 'Entregado',
      cancelado: 'Cancelado'
    };
    return map[estado] ?? estado;
  }

  onEstadoSeleccionado(idPedido: number, estado: string): void {
    this.estadoCambio.set({ id: idPedido, estado });
  }

  guardarEstado(idPedido: number): void {
    const cambio = this.estadoCambio();
    if (!cambio || cambio.id !== idPedido) {
      return;
    }
    this.guardando.set(true);
    this.error.set(null);
    this.mensaje.set(null);
    this.pedidosService.cambiarEstado(idPedido, cambio.estado).subscribe({
      next: () => {
        this.guardando.set(false);
        this.estadoCambio.set(null);
        this.mensaje.set(`Pedido #${idPedido} actualizado a ${this.etiquetaEstado(cambio.estado)}`);
        this.cargarPedidos();
      },
      error: (err) => {
        this.guardando.set(false);
        this.error.set(this.extraerError(err));
      }
    });
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

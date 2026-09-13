import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Pedido } from '../../models/pedido.models';
import { PedidosService } from '../../services/pedidos.service';
import { RecetasService } from '../../services/recetas.service';
import { Receta } from '../../models/receta.models';

@Component({
  selector: 'app-empleado-pedidos',
  imports: [DatePipe],
  templateUrl: './gestion-pedidos.html',
  styleUrl: './gestion-pedidos.css'
})
export class EmpleadoPedidosComponent implements OnInit {
  private readonly pedidosService = inject(PedidosService);
  private readonly recetasService = inject(RecetasService);
  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  readonly pedidos = signal<Pedido[]>([]);
  readonly cargando = signal(false);
  readonly guardando = signal<number | null>(null);

  readonly recetas = signal<Receta[]>([]);
  readonly cargandoRecetas = signal(false);
  readonly mostrarRecetas = signal(false);

  readonly toastActivo = signal(false);
  readonly toastTipo = signal<'cargando' | 'exito' | 'error'>('cargando');
  readonly toastTitulo = signal('');
  readonly toastMensaje = signal('');

  readonly estadoActivos = ['pendiente', 'en_preparacion'];
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
    this.pedidosService.listarTodos().subscribe({
      next: (res) => {
        this.pedidos.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.mostrarToastError('Error al cargar', this.extraerError(err));
      }
    });
  }

  etiquetaEstado(estado: string): string {
    const map: Record<string, string> = {
      pendiente: 'Pendiente',
      en_preparacion: 'En Preparación',
      enviado: 'Enviado',
      entregado: 'Entregado',
      cancelado: 'Cancelado'
    };
    return map[estado] ?? estado;
  }

  cambiarEstado(idPedido: number, nuevoEstado: string): void {
    this.guardando.set(idPedido);
    this.mostrarToastCargando(
      'Actualizando estado',
      `Pedido #${idPedido} → ${this.etiquetaEstado(nuevoEstado)}`
    );
    this.pedidosService.cambiarEstado(idPedido, nuevoEstado).subscribe({
      next: () => {
        this.guardando.set(null);
        this.mostrarToastExito(
          'Estado actualizado',
          `Pedido #${idPedido} → ${this.etiquetaEstado(nuevoEstado)}`
        );
        this.cargarPedidos();
      },
      error: (err) => {
        this.guardando.set(null);
        this.mostrarToastError('Error al actualizar', this.extraerError(err));
      }
    });
  }

  toggleRecetas(): void {
    if (this.mostrarRecetas()) {
      this.mostrarRecetas.set(false);
      return;
    }
    this.mostrarRecetas.set(true);
    if (this.recetas().length === 0) {
      this.cargandoRecetas.set(true);
      this.recetasService.listar().subscribe({
        next: (res) => {
          this.recetas.set(res.data ?? []);
          this.cargandoRecetas.set(false);
        },
        error: () => {
          this.cargandoRecetas.set(false);
        }
      });
    }
  }

  mostrarToastCargando(titulo: string, msg: string): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toastTipo.set('cargando');
    this.toastTitulo.set(titulo);
    this.toastMensaje.set(msg);
    this.toastActivo.set(true);
  }

  mostrarToastExito(titulo: string, msg: string): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toastTipo.set('exito');
    this.toastTitulo.set(titulo);
    this.toastMensaje.set(msg);
    this.toastActivo.set(true);
    this.toastTimer = setTimeout(() => this.toastActivo.set(false), 3000);
  }

  mostrarToastError(titulo: string, msg: string): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toastTipo.set('error');
    this.toastTitulo.set(titulo);
    this.toastMensaje.set(msg);
    this.toastActivo.set(true);
    this.toastTimer = setTimeout(() => this.toastActivo.set(false), 5000);
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Pedido } from '../../models/pedido.models';
import { Resena, ResenaCreateRequest } from '../../services/resenas.service';
import { PedidosService } from '../../services/pedidos.service';
import { ResenasService } from '../../services/resenas.service';

@Component({
  selector: 'app-mis-pedidos',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './mis-pedidos.html',
  styleUrl: './mis-pedidos.css'
})
export class MisPedidosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly pedidosService = inject(PedidosService);
  private readonly resenasService = inject(ResenasService);

  readonly pedidos = signal<Pedido[]>([]);
  readonly resenasPorPedido = signal<Map<number, Resena>>(new Map());
  readonly cargando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly pedidoResenaAbierto = signal<number | null>(null);

  readonly resenaForm = this.fb.nonNullable.group({
    calificacion: [0.5, [Validators.required, Validators.min(0.5), Validators.max(5.0)]],
    comentario: ['']
  });

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.pedidosService.listarMisPedidos().subscribe({
      next: (res) => {
        this.pedidos.set(res.data ?? []);
        this.cargando.set(false);
        this.cargarResenas();
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  cargarResenas(): void {
    for (const pedido of this.pedidos()) {
      this.resenasService.listarPorPedido(pedido.idPedido).subscribe({
        next: (res) => {
          if (res.data && res.data.length > 0) {
            this.resenasPorPedido.update((map) => {
              const nuevoMap = new Map(map);
              nuevoMap.set(pedido.idPedido, res.data[0]);
              return nuevoMap;
            });
          }
        },
        error: () => {}
      });
    }
  }

  puedeResenar(pedido: Pedido): boolean {
    return (
      pedido.estado === 'entregado' &&
      !this.resenasPorPedido().has(pedido.idPedido)
    );
  }

  abrirResena(idPedido: number): void {
    this.pedidoResenaAbierto.set(idPedido);
    this.resenaForm.reset({ calificacion: 0.5, comentario: '' });
  }

  cerrarResena(): void {
    this.pedidoResenaAbierto.set(null);
  }

  enviarResena(idPedido: number): void {
    if (this.resenaForm.invalid) {
      this.resenaForm.markAllAsTouched();
      return;
    }

    const value = this.resenaForm.getRawValue();
    const request: ResenaCreateRequest = {
      calificacion: value.calificacion,
      comentario: value.comentario.trim() || null
    };

    this.resenasService.crear(idPedido, request).subscribe({
      next: () => {
        this.mensaje.set('Reseña enviada correctamente');
        this.cerrarResena();
        this.cargarResenas();
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
      }
    });
  }

  incrementarCalificacion(): void {
    const actual = this.resenaForm.getRawValue().calificacion;
    if (actual < 5.0) {
      this.resenaForm.patchValue({ calificacion: Math.min(5.0, +(actual + 0.5).toFixed(1)) });
    }
  }

  decrementarCalificacion(): void {
    const actual = this.resenaForm.getRawValue().calificacion;
    if (actual > 0.5) {
      this.resenaForm.patchValue({ calificacion: Math.max(0.5, +(actual - 0.5).toFixed(1)) });
    }
  }

  estrellasCompletas(): number {
    return Math.floor(this.resenaForm.getRawValue().calificacion);
  }

  tieneMediaEstrella(): boolean {
    return this.resenaForm.getRawValue().calificacion % 1 !== 0;
  }

  estrellasVacias(): number {
    const cal = this.resenaForm.getRawValue().calificacion;
    return 5 - Math.ceil(cal);
  }

  estrellasDisplayCompletas(calificacion: number): number {
    return Math.floor(calificacion);
  }

  tieneMediaEstrellaDisplay(calificacion: number): boolean {
    return calificacion % 1 !== 0;
  }

  estrellasDisplayVacias(calificacion: number): number {
    return 5 - Math.ceil(calificacion);
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

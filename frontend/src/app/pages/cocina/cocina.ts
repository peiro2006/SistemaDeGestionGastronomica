import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { Pedido } from '../../models/pedido.models';
import { PedidosService } from '../../services/pedidos.service';

@Component({
  selector: 'app-cocina',
  imports: [DatePipe],
  templateUrl: './cocina.html',
  styleUrl: './cocina.css'
})
export class CocinaComponent implements OnInit, OnDestroy {
  private readonly pedidosService = inject(PedidosService);
  private readonly router = inject(Router);
  private refreshTimer: ReturnType<typeof setInterval> | null = null;

  readonly pedidos = signal<Pedido[]>([]);
  readonly cargando = signal(true);
  readonly ultimaActualizacion = signal<Date>(new Date());

  ngOnInit(): void {
    this.cargarPedidos();
    this.refreshTimer = setInterval(() => this.cargarPedidos(), 10000);
  }

  ngOnDestroy(): void {
    if (this.refreshTimer) clearInterval(this.refreshTimer);
  }

  cargarPedidos(): void {
    this.pedidosService.listarEnPreparacion().subscribe({
      next: (res) => {
        this.pedidos.set(res.data ?? []);
        this.ultimaActualizacion.set(new Date());
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  }

  volver(): void {
    this.router.navigate(['/empleado/pedidos']);
  }
}

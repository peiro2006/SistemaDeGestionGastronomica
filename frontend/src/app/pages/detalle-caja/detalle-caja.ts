import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Caja, CajaResumen } from '../../models/caja.models';
import { Pedido } from '../../models/pedido.models';
import { CajaService } from '../../services/caja.service';

@Component({
  selector: 'app-detalle-caja',
  imports: [DatePipe, RouterLink],
  templateUrl: './detalle-caja.html',
  styleUrl: './detalle-caja.css'
})
export class DetalleCajaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  protected readonly cajaService = inject(CajaService);

  readonly caja = signal<Caja | null>(null);
  readonly resumen = signal<CajaResumen | null>(null);
  readonly movimientos = signal<Pedido[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.cargarDatos(id);
    }
  }

  cargarDatos(idCaja: number): void {
    this.cargando.set(true);
    this.error.set(null);

    this.cajaService.obtener(idCaja).subscribe({
      next: (res) => {
        this.caja.set(res.data);
        this.cargarMovimientos(idCaja);
        this.cargarResumen(idCaja);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  cargarMovimientos(idCaja: number): void {
    this.cajaService.obtenerMovimientos(idCaja).subscribe({
      next: (res) => {
        this.movimientos.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  cargarResumen(idCaja: number): void {
    this.cajaService.obtenerResumen(idCaja).subscribe({
      next: (res) => {
        this.resumen.set(res.data);
      },
      error: () => {}
    });
  }

  totalMovimientos(): number {
    return this.movimientos().reduce((sum, p) => sum + p.total, 0);
  }

  etiquetaMetodoPago(metodo: string): string {
    const map: Record<string, string> = {
      EFECTIVO: 'Efectivo',
      DEBITO: 'Debito',
      TARJETA_CREDITO: 'Credito',
      TRANSFERENCIA: 'Transferencia'
    };
    return map[metodo] ?? metodo;
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

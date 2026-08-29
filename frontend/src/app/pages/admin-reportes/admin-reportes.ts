import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EstadoPedido, ReporteRes } from '../../models/reporte.models';
import { ReporteService } from '../../services/reporte.service';

@Component({
  selector: 'app-admin-reportes',
  imports: [FormsModule, RouterLink, DatePipe, CurrencyPipe],
  templateUrl: './admin-reportes.html',
  styleUrl: './admin-reportes.css'
})
export class AdminReportesComponent implements OnInit {
  private readonly reporteService = inject(ReporteService);

  readonly reporte = signal<ReporteRes | null>(null);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly desde = signal<string>('');
  readonly hasta = signal<string>('');
  readonly preset = signal<string>('30');

  readonly estados: EstadoPedido[] = ['pendiente', 'en_preparacion', 'enviado', 'entregado', 'cancelado'];
  readonly etiquetasEstado: Record<EstadoPedido, string> = {
    pendiente: 'Pendiente',
    en_preparacion: 'En preparacion',
    enviado: 'Enviado',
    entregado: 'Entregado',
    cancelado: 'Cancelado'
  };

  ngOnInit(): void {
    this.aplicarPreset('30');
  }

  aplicarPreset(key: string): void {
    this.preset.set(key);

    const hasta = new Date();
    const desde = new Date();
    if (key === 'todo') {
      desde.setFullYear(2000, 0, 1);
    } else {
      const dias = Number(key);
      desde.setDate(desde.getDate() - (dias - 1));
    }

    this.desde.set(toDateInput(desde));
    this.hasta.set(toDateInput(hasta));
    this.cargar(desde.getTime(), hasta.getTime());
  }

  aplicarFechas(): void {
    this.preset.set('custom');

    const desdeStr = this.desde();
    const hastaStr = this.hasta();
    if (!desdeStr || !hastaStr) {
      this.error.set('Debe seleccionar una fecha de inicio y una fecha de fin.');
      return;
    }

    const desdeMs = new Date(desdeStr + 'T00:00:00').getTime();
    const hastaMs = new Date(hastaStr + 'T23:59:59').getTime();
    this.cargar(desdeMs, hastaMs);
  }

  maxPorEstado(): number {
    const data = this.reporte();
    if (!data) {
      return 0;
    }
    return Math.max(...this.estados.map((e) => data.pedidosPorEstado?.[e] ?? 0), 1);
  }

  estadoVisible(estado: EstadoPedido): boolean {
    return (this.reporte()?.pedidosPorEstado?.hasOwnProperty(estado) ?? false)
      || (this.reporte()?.pedidosPorEstado?.[estado] ?? 0) > 0;
  }

  private cargar(desde: number, hasta: number): void {
    this.cargando.set(true);
    this.error.set(null);
    this.reporteService.reporte(desde, hasta).subscribe({
      next: (res) => {
        this.reporte.set(res.data ?? null);
        this.cargando.set(false);
      },
      error: (err) => {
        const httpError = err as { error?: { message?: string; errors?: string[] | null } };
        this.error.set(httpError?.error?.errors?.length ? httpError.error.errors.join(' - ') : (httpError?.error?.message ?? 'No se pudo generar el reporte.'));
        this.cargando.set(false);
      }
    });
  }
}

function toDateInput(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}
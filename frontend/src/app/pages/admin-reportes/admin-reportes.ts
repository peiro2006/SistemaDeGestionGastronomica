import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';
import { EstadoPedido, ReporteRes } from '../../models/reporte.models';
import { ReporteService } from '../../services/reporte.service';
import { CajaService } from '../../services/caja.service';
import { CajaArqueo } from '../../models/caja.models';
import { Pedido } from '../../models/pedido.models';

@Component({
  selector: 'app-admin-reportes',
  imports: [FormsModule, RouterLink, RouterLinkActive, DatePipe, CurrencyPipe, AdminSidebarComponent],
  templateUrl: './admin-reportes.html',
  styleUrl: './admin-reportes.css'
})
export class AdminReportesComponent implements OnInit {
  private readonly reporteService = inject(ReporteService);
  private readonly cajaService = inject(CajaService);

  readonly reporte = signal<ReporteRes | null>(null);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly desde = signal<string>('');
  readonly hasta = signal<string>('');
  readonly preset = signal<string>('30');

  readonly arqueos = signal<CajaArqueo[]>([]);
  readonly cargandoArqueos = signal(false);

  readonly modalMovimientosAbierto = signal(false);
  readonly arqueoSeleccionado = signal<CajaArqueo | null>(null);
  readonly movimientosArqueo = signal<Pedido[]>([]);
  readonly cargandoMovimientos = signal(false);

  readonly estados: EstadoPedido[] = ['pendiente', 'en_preparacion', 'enviado', 'entregado', 'cancelado'];
  readonly etiquetasEstado: Record<EstadoPedido, string> = {
    pendiente: 'Pendiente',
    en_preparacion: 'En preparacion',
    enviado: 'Enviado',
    entregado: 'Entregado',
    cancelado: 'Cancelado'
  };

  readonly etiquetasMetodoPago: Record<string, string> = {
    EFECTIVO: 'Efectivo',
    DEBITO: 'Debito',
    TARJETA_CREDITO: 'Credito',
    TRANSFERENCIA: 'Transferencia'
  };

  ngOnInit(): void {
    this.aplicarPreset('30');
    this.cargarArqueos();
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

  cargarArqueos(): void {
    this.cargandoArqueos.set(true);
    this.cajaService.listarArqueos().subscribe({
      next: (res) => {
        this.arqueos.set(res.data ?? []);
        this.cargandoArqueos.set(false);
      },
      error: () => {
        this.cargandoArqueos.set(false);
      }
    });
  }

  verMovimientos(arqueo: CajaArqueo): void {
    this.arqueoSeleccionado.set(arqueo);
    this.cargandoMovimientos.set(true);
    this.modalMovimientosAbierto.set(true);

    this.cajaService.obtenerArqueo(arqueo.idArqueo).subscribe({
      next: (res) => {
        this.movimientosArqueo.set(res.data?.movimientos ?? []);
        this.cargandoMovimientos.set(false);
      },
      error: () => {
        this.movimientosArqueo.set([]);
        this.cargandoMovimientos.set(false);
      }
    });
  }

  cerrarModalMovimientos(): void {
    this.modalMovimientosAbierto.set(false);
    this.arqueoSeleccionado.set(null);
    this.movimientosArqueo.set([]);
  }

  formatearFecha(fecha: string): string {
    const d = new Date(fecha);
    return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`;
  }

  etiquetaEstado(estado: string): string {
    return this.etiquetasEstado[estado as EstadoPedido] ?? estado;
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

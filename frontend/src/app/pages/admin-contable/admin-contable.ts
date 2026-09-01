import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MovimientoContable, MovimientoPageRes, MetodoPago, TipoMovimiento } from '../../models/movimiento.models';
import { MovimientoContableService } from '../../services/movimiento-contable.service';

@Component({
  selector: 'app-admin-contable',
  imports: [FormsModule, RouterLink, DatePipe, CurrencyPipe],
  templateUrl: './admin-contable.html',
  styleUrl: './admin-contable.css'
})
export class AdminContableComponent implements OnInit {
  private readonly service = inject(MovimientoContableService);

  readonly data = signal<MovimientoPageRes | null>(null);
  readonly cargando = signal(false);
  readonly guardando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly detalle = signal<MovimientoContable | null>(null);
  readonly mostrarDetalle = signal(false);

  readonly crearModo = signal(false);

  // filtros
  readonly fTipo = signal('');
  readonly fMetodo = signal('');
  readonly fDesde = signal('');
  readonly fHasta = signal('');
  readonly fMontoMin = signal<number | null>(null);
  readonly fMontoMax = signal<number | null>(null);
  readonly fConcepto = signal('');
  readonly pagina = signal(0);
  readonly tamanio = signal(20);

  // form registro
  readonly nTipo = signal<TipoMovimiento>('INGRESO');
  readonly nMonto = signal<number | null>(null);
  readonly nConcepto = signal('');
  readonly nMetodo = signal<string>('EFECTIVO');

  readonly metodos = ['EFECTIVO', 'DEBITO', 'TRANSFERENCIA'];
  readonly tipos: TipoMovimiento[] = ['INGRESO', 'EGRESO'];

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.service.listar({
      tipo: this.fTipo() || undefined,
      metodoPago: this.fMetodo() || undefined,
      desde: this.fDesde() ? this.aInicioDia(this.fDesde()) : undefined,
      hasta: this.fHasta() ? this.aFinDia(this.fHasta()) : undefined,
      montoMin: this.fMontoMin() ?? undefined,
      montoMax: this.fMontoMax() ?? undefined,
      concepto: this.fConcepto() || undefined,
      page: this.pagina(),
      size: this.tamanio()
    }).subscribe({
      next: (res) => {
        this.data.set(res.data ?? null);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  aplicarFiltros(): void {
    this.pagina.set(0);
    this.cargar();
  }

  limpiarFiltros(): void {
    this.fTipo.set('');
    this.fMetodo.set('');
    this.fDesde.set('');
    this.fHasta.set('');
    this.fMontoMin.set(null);
    this.fMontoMax.set(null);
    this.fConcepto.set('');
    this.pagina.set(0);
    this.cargar();
  }

  irPagina(p: number): void {
    if (p < 0 || p >= (this.data()?.totalPages ?? 0)) {
      return;
    }
    this.pagina.set(p);
    this.cargar();
  }

  onTodoElPeriodo(): void {
    const hoy = new Date();
    const hoyStr = toDateInput(hoy);
    const inicioMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
    this.fDesde.set(toDateInput(inicioMes));
    this.fHasta.set(hoyStr);
    this.aplicarFiltros();
  }

  verDetalle(mov: MovimientoContable): void {
    this.detalle.set(mov);
    this.mostrarDetalle.set(true);
  }

  cerrarDetalle(): void {
    this.mostrarDetalle.set(false);
    this.detalle.set(null);
  }

  exportar(): void {
    this.service.exportar({
      tipo: this.fTipo() || undefined,
      metodoPago: this.fMetodo() || undefined,
      desde: this.fDesde() ? this.aInicioDia(this.fDesde()) : undefined,
      hasta: this.fHasta() ? this.aFinDia(this.fHasta()) : undefined,
      montoMin: this.fMontoMin() ?? undefined,
      montoMax: this.fMontoMax() ?? undefined,
      concepto: this.fConcepto() || undefined
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'movimientos_contables.csv';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => this.error.set(this.extraerError(err))
    });
  }

  registrar(): void {
    if (this.nMonto() == null || this.nMonto()! <= 0) {
      this.error.set('Debe indicar un monto mayor a cero.');
      return;
    }
    if (this.nTipo() === 'INGRESO' && !this.nMetodo()) {
      this.error.set('Debe indicar el metodo de pago del ingreso.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    this.service.registrar({
      tipo: this.nTipo(),
      monto: this.nMonto()!,
      concepto: this.nConcepto().trim() || null,
      metodoPago: this.nTipo() === 'INGRESO' ? this.nMetodo() as MetodoPago : null
    }).subscribe({
      next: () => {
        this.guardando.set(false);
        this.mensaje.set('Movimiento registrado correctamente');
        this.nMonto.set(null);
        this.nConcepto.set('');
        this.nTipo.set('INGRESO');
        this.nMetodo.set('EFECTIVO');
        this.crearModo.set(false);
        this.cargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.error.set(this.extraerError(err));
      }
    });
  }

  private aInicioDia(dateStr: string): number {
    return new Date(dateStr + 'T00:00:00').getTime();
  }

  private aFinDia(dateStr: string): number {
    return new Date(dateStr + 'T23:59:59').getTime();
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

function toDateInput(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

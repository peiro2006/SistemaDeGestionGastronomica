import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';
import { Producto } from '../../models/producto.models';
import { Insumo } from '../../models/insumo.models';
import { StockMovimiento, TipoMovimientoStock } from '../../models/stock.models';
import { ProductosService } from '../../services/productos.service';
import { InsumosService } from '../../services/insumos.service';
import { StockService } from '../../services/stock.service';

@Component({
  selector: 'app-admin-stock',
  imports: [ReactiveFormsModule, RouterLink, RouterLinkActive, DatePipe, AdminSidebarComponent],
  templateUrl: './admin-stock.html',
  styleUrl: './admin-stock.css'
})
export class AdminStockComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly productosService = inject(ProductosService);
  private readonly insumosService = inject(InsumosService);
  private readonly stockService = inject(StockService);

  readonly productos = signal<Producto[]>([]);
  readonly insumos = signal<Insumo[]>([]);
  readonly movimientos = signal<StockMovimiento[]>([]);
  readonly filtroRecurso = signal<'todos' | 'producto' | 'insumo'>('todos');
  readonly filtroIdProducto = signal(0);
  readonly filtroIdInsumo = signal(0);
  readonly cargando = signal(false);
  readonly guardando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    destino: ['producto' as 'producto' | 'insumo', [Validators.required]],
    idProducto: [0],
    idInsumo: [0],
    tipo: ['INGRESO' as TipoMovimientoStock, [Validators.required]],
    cantidad: [1, [Validators.required, Validators.min(1)]],
    motivo: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando.set(true);
    this.productosService.listar().subscribe({
      next: (res) => {
        this.productos.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
    this.insumosService.listar().subscribe({
      next: (res) => this.insumos.set(res.data ?? []),
      error: (err) => this.error.set(this.extraerError(err))
    });
    this.cargarMovimientos();
  }

  cargarMovimientos(): void {
    this.stockService
      .listar(this.filtrosMovimientos())
      .subscribe({
        next: (res) => this.movimientos.set(res.data ?? []),
        error: (err) => this.error.set(this.extraerError(err))
      });
  }

  cambiarFiltroRecurso(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filtroRecurso.set(select.value as 'todos' | 'producto' | 'insumo');
    this.filtroIdProducto.set(0);
    this.filtroIdInsumo.set(0);
    this.cargarMovimientos();
  }

  cambiarFiltroProducto(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filtroIdProducto.set(Number(select.value));
    this.cargarMovimientos();
  }

  cambiarFiltroInsumo(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filtroIdInsumo.set(Number(select.value));
    this.cargarMovimientos();
  }

  limpiarFiltros(): void {
    this.filtroRecurso.set('todos');
    this.filtroIdProducto.set(0);
    this.filtroIdInsumo.set(0);
    this.cargarMovimientos();
  }

  private filtrosMovimientos(): { idProducto?: number; idInsumo?: number } {
    const filtros: { idProducto?: number; idInsumo?: number } = {};
    if (this.filtroRecurso() === 'producto' && this.filtroIdProducto()) {
      filtros.idProducto = this.filtroIdProducto();
    }
    if (this.filtroRecurso() === 'insumo' && this.filtroIdInsumo()) {
      filtros.idInsumo = this.filtroIdInsumo();
    }
    return filtros;
  }

  ajustar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    if (value.destino === 'producto' && !value.idProducto) {
      this.error.set('Debe seleccionar un producto.');
      return;
    }
    if (value.destino === 'insumo' && !value.idInsumo) {
      this.error.set('Debe seleccionar un insumo.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    this.stockService
      .ajustar({
        idProducto: value.destino === 'producto' ? value.idProducto : null,
        idInsumo: value.destino === 'insumo' ? value.idInsumo : null,
        tipo: value.tipo,
        cantidad: value.cantidad,
        motivo: value.motivo.trim()
      })
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.mensaje.set('Stock actualizado correctamente');
          this.form.patchValue({ cantidad: 1, motivo: '' });
          this.cargarDatos();
        },
        error: (err) => {
          this.guardando.set(false);
          this.error.set(this.extraerError(err));
        }
      });
  }

  actualizarDestino(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.form.patchValue({ idProducto: 0, idInsumo: 0 });
  }

  stockBajoProducto(producto: Producto): boolean {
    return producto.stockActual !== null && producto.stockMinimo !== null && producto.stockActual < producto.stockMinimo;
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

import { Component, OnInit, ViewChild, ElementRef, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';
import { Producto } from '../../models/producto.models';
import { Insumo } from '../../models/insumo.models';
import { StockMovimiento, TipoMovimientoStock } from '../../models/stock.models';
import { ProductosService } from '../../services/productos.service';
import { InsumosService } from '../../services/insumos.service';
import { StockService } from '../../services/stock.service';

@Component({
  selector: 'app-admin-stock',
  imports: [ReactiveFormsModule, RouterLink, DatePipe, AdminSidebarComponent],
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
  readonly guardandoInsumo = signal(false);
  readonly mostrarFormInsumo = signal(false);
  readonly editandoInsumo = signal<Insumo | null>(null);
  readonly nombreInsumo = signal('');
  readonly unidadMedida = signal('');
  readonly stockInicial = signal(0);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  @ViewChild('inputNombreInsumo') inputNombreInsumo!: ElementRef<HTMLInputElement>;
  @ViewChild('inputStockInicial') inputStockInicial!: ElementRef<HTMLInputElement>;

  readonly form = this.fb.nonNullable.group({
    destino: ['producto' as 'producto' | 'insumo', [Validators.required]],
    idProducto: [0],
    idInsumo: [0],
    tipo: ['INGRESO' as TipoMovimientoStock, [Validators.required]],
    cantidad: [1, [Validators.required, Validators.min(1)]],
    motivo: ['', [Validators.required]],
    montoCompra: [0]
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
        motivo: value.motivo.trim(),
        montoCompra: value.tipo === 'INGRESO' && value.montoCompra > 0 ? value.montoCompra : null
      })
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.mensaje.set('Stock actualizado correctamente');
          this.form.patchValue({ cantidad: 1, motivo: '', montoCompra: 0 });
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

  toggleFormInsumo(): void {
    this.mostrarFormInsumo.update(v => !v);
    if (!this.mostrarFormInsumo()) {
      this.editandoInsumo.set(null);
      this.nombreInsumo.set('');
      this.unidadMedida.set('');
      this.stockInicial.set(0);
    }
  }

  editarInsumo(insumo: Insumo): void {
    this.editandoInsumo.set(insumo);
    this.nombreInsumo.set(insumo.nombreInsumo);
    this.unidadMedida.set(insumo.unidadMedida);
    this.stockInicial.set(insumo.stockActual);
    this.mostrarFormInsumo.set(true);
    setTimeout(() => {
      if (this.inputNombreInsumo) this.inputNombreInsumo.nativeElement.value = insumo.nombreInsumo;
      if (this.inputStockInicial) this.inputStockInicial.nativeElement.value = String(insumo.stockActual);
    });
  }

  guardarInsumo(): void {
    if (!this.nombreInsumo().trim() || !this.unidadMedida().trim()) {
      this.error.set('Complete nombre y unidad de medida.');
      return;
    }

    this.guardandoInsumo.set(true);
    this.error.set(null);

    const data = {
      nombreInsumo: this.nombreInsumo().trim(),
      unidadMedida: this.unidadMedida().trim(),
      stockActual: this.stockInicial()
    };

    const editando = this.editandoInsumo();

    const request$ = editando
      ? this.insumosService.actualizar(editando.idInsumo, data)
      : this.insumosService.crear(data);

    request$.subscribe({
      next: () => {
        this.guardandoInsumo.set(false);
        this.mostrarFormInsumo.set(false);
        this.editandoInsumo.set(null);
        this.nombreInsumo.set('');
        this.unidadMedida.set('');
        this.stockInicial.set(0);
        this.mensaje.set(editando ? 'Insumo actualizado correctamente' : 'Insumo creado correctamente');
        this.cargarDatos();
      },
      error: (err) => {
        this.guardandoInsumo.set(false);
        this.error.set(this.extraerError(err));
      }
    });
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

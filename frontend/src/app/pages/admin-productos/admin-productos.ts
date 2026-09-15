import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Producto } from '../../models/producto.models';
import { Receta } from '../../models/receta.models';
import { Insumo } from '../../models/insumo.models';
import { Notificacion } from '../../services/notificaciones.service';
import { ProductosService } from '../../services/productos.service';
import { RecetasService } from '../../services/recetas.service';
import { InsumosService } from '../../services/insumos.service';
import { NotificacionesService } from '../../services/notificaciones.service';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';

interface IngredienteForm {
  idInsumo: number;
  nombreInsumo: string;
  unidadMedida: string;
  cantidad: number;
}

@Component({
  selector: 'app-admin-productos',
  imports: [ReactiveFormsModule, RouterLink, DatePipe, AdminSidebarComponent],
  templateUrl: './admin-productos.html',
  styleUrl: './admin-productos.css'
})
export class AdminProductosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly productosService = inject(ProductosService);
  private readonly recetasService = inject(RecetasService);
  private readonly insumosService = inject(InsumosService);
  private readonly notificacionesService = inject(NotificacionesService);

  readonly productos = signal<Producto[]>([]);
  readonly recetas = signal<Receta[]>([]);
  readonly insumos = signal<Insumo[]>([]);
  readonly notificaciones = signal<Notificacion[]>([]);
  readonly notificacionesNoLeidas = signal(0);
  readonly mostrarNotificaciones = signal(false);
  readonly ingredientes = signal<IngredienteForm[]>([this.nuevoIngrediente()]);
  readonly productoEditando = signal<Producto | null>(null);
  readonly cargando = signal(false);
  readonly guardandoProducto = signal(false);
  readonly guardandoReceta = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly recetaError = signal<string | null>(null);
  readonly stockMaximoMap = signal<Record<number, number>>({});
  readonly stockMaximoForm = signal<number>(0);
  readonly recetaEditando = signal<Receta | null>(null);
  readonly mostrarModalReceta = signal(false);
  readonly ingredientesModal = signal<IngredienteForm[]>([]);

  readonly productoForm = this.fb.nonNullable.group({
    nombreProducto: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    descripcion: ['', [Validators.required]],
    precio: ['', [Validators.required, Validators.pattern(/^(?!0+(\.0+)?$)\d+(\.\d{1,2})?$/)]],
    categoria: ['', [Validators.required]],
    imagenUrl: [''],
    stockActual: [0, [Validators.required, Validators.min(0)]],
    stockMinimo: [0, [Validators.required, Validators.min(0)]],
    idReceta: [0, [Validators.required, Validators.min(1)]]
  });

  readonly recetaForm = this.fb.nonNullable.group({
    nombreReceta: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    descripcionReceta: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.cargarRecetas();
    this.cargarProductos();
    this.cargarNotificaciones();
    this.cargarInsumos();
  }

  cargarProductos(): void {
    this.cargando.set(true);
    this.productosService.listar().subscribe({
      next: (res) => {
        this.productos.set(res.data ?? []);
        this.cargarStockMaximo();
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  cargarStockMaximo(): void {
    this.productosService.obtenerStockMaximoTodos().subscribe({
      next: (res) => this.stockMaximoMap.set(res.data ?? {}),
      error: () => {}
    });
  }

  cargarInsumos(): void {
    this.insumosService.listar().subscribe({
      next: (res) => this.insumos.set(res.data ?? []),
      error: () => {}
    });
  }

  onRecetaChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const idReceta = Number(select.value);
    if (idReceta > 0) {
      this.productosService.obtenerStockMaximoPorReceta(idReceta).subscribe({
        next: (res) => this.stockMaximoForm.set(res.data?.stockMaximo ?? 0),
        error: () => this.stockMaximoForm.set(0)
      });
    } else {
      this.stockMaximoForm.set(0);
    }
  }

  cargarRecetas(): void {
    this.recetasService.listar().subscribe({
      next: (res) => this.recetas.set(res.data ?? []),
      error: (err) => this.error.set(this.extraerError(err))
    });
  }

  guardarProducto(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return;
    }

    const value = this.productoForm.getRawValue();
    const stockMax = this.stockMaximoForm();
    if (stockMax > 0 && value.stockActual > stockMax) {
      this.error.set(`El stock para vender (${value.stockActual}) no puede superar el stock maximo (${stockMax}) calculado segun la receta.`);
      return;
    }

    this.guardandoProducto.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    const editando = this.productoEditando();

    if (editando) {
      this.productosService
        .actualizar(editando.idProducto, {
          nombreProducto: value.nombreProducto.trim(),
          descripcion: value.descripcion.trim(),
          precio: value.precio.trim(),
          categoria: value.categoria.trim(),
          imagenUrl: value.imagenUrl.trim() || null,
          stockMinimo: value.stockMinimo,
          idReceta: value.idReceta
        })
        .subscribe(this.productoHandler('Producto actualizado correctamente'));
      return;
    }

    this.productosService
      .crear({
        nombreProducto: value.nombreProducto.trim(),
        descripcion: value.descripcion.trim(),
        precio: value.precio.trim(),
        categoria: value.categoria.trim(),
        imagenUrl: value.imagenUrl.trim() || null,
        stockActual: value.stockActual,
        stockMinimo: value.stockMinimo,
        idReceta: value.idReceta
      })
      .subscribe(this.productoHandler('Producto creado correctamente'));
  }

  editar(producto: Producto): void {
    this.productoEditando.set(producto);
    this.productoForm.patchValue({
      nombreProducto: producto.nombreProducto,
      descripcion: producto.descripcion,
      precio: producto.precio,
      categoria: producto.categoria ?? '',
      imagenUrl: producto.imagenUrl ?? '',
      stockActual: producto.stockActual ?? 0,
      stockMinimo: producto.stockMinimo ?? 0,
      idReceta: producto.idReceta ?? 0
    });
    if (producto.idReceta) {
      this.productosService.obtenerStockMaximo(producto.idReceta).subscribe({
        next: (res) => this.stockMaximoForm.set(res.data?.stockMaximo ?? 0),
        error: () => this.stockMaximoForm.set(0)
      });
    }
    this.mensaje.set(null);
    this.error.set(null);
  }

  cancelarEdicion(): void {
    this.productoEditando.set(null);
    this.stockMaximoForm.set(0);
    this.productoForm.reset({
      nombreProducto: '',
      descripcion: '',
      precio: '',
      categoria: '',
      imagenUrl: '',
      stockActual: 0,
      stockMinimo: 0,
      idReceta: 0
    });
  }

  cambiarEstado(producto: Producto): void {
    this.error.set(null);
    this.mensaje.set(null);
    this.productosService.cambiarEstado(producto.idProducto, !producto.activo).subscribe({
      next: () => {
        this.mensaje.set('Estado actualizado correctamente');
        this.cargarProductos();
      },
      error: (err) => this.error.set(this.extraerError(err))
    });
  }

  guardarReceta(): void {
    if (this.recetaForm.invalid || this.ingredientesInvalidos()) {
      this.recetaForm.markAllAsTouched();
      this.recetaError.set('Complete nombre, descripcion y al menos un ingrediente con cantidad mayor a 0.');
      return;
    }

    this.guardandoReceta.set(true);
    this.recetaError.set(null);

    const receta = this.recetaForm.getRawValue();
    this.recetasService
      .crear({
        nombreReceta: receta.nombreReceta.trim(),
        descripcionReceta: receta.descripcionReceta.trim(),
        ingredientesReceta: null,
        ingredientes: this.ingredientes().map((ingrediente) => ({
          idInsumo: ingrediente.idInsumo || null,
          nombreInsumo: ingrediente.nombreInsumo.trim(),
          unidadMedida: ingrediente.unidadMedida.trim(),
          cantidad: ingrediente.cantidad
        }))
      })
      .subscribe({
        next: () => {
          this.guardandoReceta.set(false);
          this.recetaForm.reset({ nombreReceta: '', descripcionReceta: '' });
          this.ingredientes.set([this.nuevoIngrediente()]);
          this.mensaje.set('Receta creada correctamente');
          this.cargarRecetas();
        },
        error: (err) => {
          this.guardandoReceta.set(false);
          this.recetaError.set(this.extraerError(err));
        }
      });
  }

  agregarIngrediente(): void {
    this.ingredientes.update((ingredientes) => [...ingredientes, this.nuevoIngrediente()]);
  }

  quitarIngrediente(index: number): void {
    this.ingredientes.update((ingredientes) => ingredientes.filter((_, i) => i !== index));
  }

  actualizarIngrediente(index: number, campo: keyof IngredienteForm, event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = campo === 'cantidad' ? Number(input.value) : input.value;
    this.ingredientes.update((ingredientes) =>
      ingredientes.map((ingrediente, i) =>
        i === index ? { ...ingrediente, [campo]: value } : ingrediente
      )
    );
  }

  private productoHandler(mensaje: string) {
    return {
      next: () => {
        this.guardandoProducto.set(false);
        this.mensaje.set(mensaje);
        this.cancelarEdicion();
        this.cargarProductos();
      },
      error: (err: unknown) => {
        this.guardandoProducto.set(false);
        this.error.set(this.extraerError(err));
      }
    };
  }

  seleccionarInsumo(index: number, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const idInsumo = Number(select.value);
    const insumo = this.insumos().find(i => i.idInsumo === idInsumo);
    this.ingredientes.update((ingredientes) =>
      ingredientes.map((ingrediente, i) =>
        i === index
          ? {
              ...ingrediente,
              idInsumo,
              nombreInsumo: insumo?.nombreInsumo ?? '',
              unidadMedida: insumo?.unidadMedida ?? ''
            }
          : ingrediente
      )
    );
  }

  editarReceta(receta: Receta): void {
    this.recetaEditando.set(receta);
    this.recetaForm.patchValue({
      nombreReceta: receta.nombreReceta,
      descripcionReceta: receta.descripcionReceta
    });
    const ingredientesCargados: IngredienteForm[] = (receta.ingredientes ?? []).map((ing) => ({
      idInsumo: ing.idInsumo,
      nombreInsumo: ing.nombreInsumo ?? '',
      unidadMedida: ing.unidadMedida ?? '',
      cantidad: ing.cantidad
    }));
    this.ingredientesModal.set(ingredientesCargados.length > 0 ? ingredientesCargados : [this.nuevoIngrediente()]);
    this.recetaError.set(null);
    this.mostrarModalReceta.set(true);
  }

  cerrarModalReceta(): void {
    this.mostrarModalReceta.set(false);
    this.recetaEditando.set(null);
    this.recetaForm.reset({ nombreReceta: '', descripcionReceta: '' });
    this.ingredientesModal.set([]);
    this.recetaError.set(null);
  }

  agregarIngredienteModal(): void {
    this.ingredientesModal.update((ingredientes) => [...ingredientes, this.nuevoIngrediente()]);
  }

  quitarIngredienteModal(index: number): void {
    this.ingredientesModal.update((ingredientes) => ingredientes.filter((_, i) => i !== index));
  }

  actualizarIngredienteModal(index: number, campo: keyof IngredienteForm, event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = campo === 'cantidad' ? Number(input.value) : input.value;
    this.ingredientesModal.update((ingredientes) =>
      ingredientes.map((ingrediente, i) =>
        i === index ? { ...ingrediente, [campo]: value } : ingrediente
      )
    );
  }

  seleccionarInsumoModal(index: number, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const idInsumo = Number(select.value);
    const insumo = this.insumos().find(i => i.idInsumo === idInsumo);
    this.ingredientesModal.update((ingredientes) =>
      ingredientes.map((ingrediente, i) =>
        i === index
          ? {
              ...ingrediente,
              idInsumo,
              nombreInsumo: insumo?.nombreInsumo ?? '',
              unidadMedida: insumo?.unidadMedida ?? ''
            }
          : ingrediente
      )
    );
  }

  guardarEdicionReceta(): void {
    const receta = this.recetaEditando();
    if (!receta) return;

    if (this.recetaForm.invalid || this.ingredientesModal().some((ing) => !ing.idInsumo || ing.cantidad <= 0)) {
      this.recetaForm.markAllAsTouched();
      this.recetaError.set('Complete nombre, descripcion y al menos un ingrediente con cantidad mayor a 0.');
      return;
    }

    this.guardandoReceta.set(true);
    this.recetaError.set(null);

    const value = this.recetaForm.getRawValue();
    this.recetasService
      .actualizar(receta.idReceta, {
        nombreReceta: value.nombreReceta.trim(),
        descripcionReceta: value.descripcionReceta.trim(),
        ingredientesReceta: null,
        ingredientes: this.ingredientesModal().map((ingrediente) => ({
          idInsumo: ingrediente.idInsumo || null,
          nombreInsumo: ingrediente.nombreInsumo.trim(),
          unidadMedida: ingrediente.unidadMedida.trim(),
          cantidad: ingrediente.cantidad
        }))
      })
      .subscribe({
        next: () => {
          this.guardandoReceta.set(false);
          this.cerrarModalReceta();
          this.mensaje.set('Receta actualizada correctamente');
          this.cargarRecetas();
        },
        error: (err) => {
          this.guardandoReceta.set(false);
          this.recetaError.set(this.extraerError(err));
        }
      });
  }

  private ingredientesInvalidos(): boolean {
    return this.ingredientes().some(
      (ingrediente) =>
        !ingrediente.idInsumo || ingrediente.cantidad <= 0
    );
  }

  private nuevoIngrediente(): IngredienteForm {
    return { idInsumo: 0, nombreInsumo: '', unidadMedida: '', cantidad: 1 };
  }

  cargarNotificaciones(): void {
    this.notificacionesService.listar(true).subscribe({
      next: (res) => {
        this.notificaciones.set(res.data ?? []);
        this.notificacionesNoLeidas.set((res.data ?? []).length);
      },
      error: () => {}
    });
  }

  toggleNotificaciones(): void {
    this.mostrarNotificaciones.update((v) => !v);
    if (this.mostrarNotificaciones()) {
      this.notificacionesService.listar().subscribe({
        next: (res) => this.notificaciones.set(res.data ?? []),
        error: () => {}
      });
    }
  }

  marcarNotificacionLeida(idNotificacion: number): void {
    this.notificacionesService.marcarLeida(idNotificacion).subscribe({
      next: () => {
        this.notificaciones.update((notifs) =>
          notifs.map((n) => (n.idNotificacion === idNotificacion ? { ...n, leida: true } : n))
        );
        this.notificacionesNoLeidas.update((n) => Math.max(0, n - 1));
      },
      error: () => {}
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

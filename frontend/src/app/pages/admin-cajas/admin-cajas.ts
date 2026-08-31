import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CajaService } from '../../services/caja.service';
import { Caja, CajaCreateRequest, CajaUpdateRequest, CajaEstadoRequest } from '../../models/caja.models';

@Component({
  selector: 'app-admin-cajas',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './admin-cajas.html',
  styleUrl: './admin-cajas.css'
})
export class AdminCajasComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  protected readonly cajaService = inject(CajaService);

  readonly cajas = signal<Caja[]>([]);
  readonly cargando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly modalAbierto = signal(false);
  readonly editando = signal<Caja | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: [''],
    moneda: ['ARS', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]],
    montoInicial: [0, [Validators.required, Validators.min(0)]],
    password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]]
  });

  ngOnInit(): void {
    this.cargarCajas();
  }

  cargarCajas(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.cajaService.listarTodas().subscribe({
      next: (res) => {
        this.cajas.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  abrirModalCrear(): void {
    this.editando.set(null);
    this.form.reset({ nombre: '', descripcion: '', montoInicial: 0, password: '' });
    this.modalAbierto.set(true);
  }

  abrirModalEditar(caja: Caja): void {
    this.editando.set(caja);
    this.form.patchValue({
      nombre: caja.nombre,
      descripcion: caja.descripcion ?? '',
      montoInicial: caja.montoInicial,
      password: ''
    });
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
    this.editando.set(null);
    this.error.set(null);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const cajaEditando = this.editando();

    if (cajaEditando) {
      const request: CajaUpdateRequest = {
        nombre: value.nombre,
        descripcion: value.descripcion || null,
        montoInicial: value.montoInicial,
        password: value.password || undefined
      };
      this.cajaService.actualizar(cajaEditando.idCaja, request).subscribe({
        next: () => {
          this.mensaje.set('Caja actualizada correctamente');
          this.cerrarModal();
          this.cargarCajas();
        },
        error: (err) => this.error.set(this.extraerError(err))
      });
    } else {
      const request: CajaCreateRequest = {
        nombre: value.nombre,
        descripcion: value.descripcion || null,
        moneda: value.moneda,
        montoInicial: value.montoInicial,
        password: value.password
      };
      this.cajaService.crear(request).subscribe({
        next: () => {
          this.mensaje.set('Caja creada correctamente');
          this.cerrarModal();
          this.cargarCajas();
        },
        error: (err) => this.error.set(this.extraerError(err))
      });
    }
  }

  onEstadoChange(caja: Caja, event: Event): void {
    const nuevoEstado = (event.target as HTMLSelectElement).value as 'INACTIVA' | 'ACTIVA' | 'NO_DISPONIBLE';
    this.cambiarEstado(caja, nuevoEstado);
  }

  cambiarEstado(caja: Caja, nuevoEstado: 'INACTIVA' | 'ACTIVA' | 'NO_DISPONIBLE'): void {
    if (caja.estado === nuevoEstado) return;

    const request: CajaEstadoRequest = { estado: nuevoEstado };
    this.cajaService.cambiarEstado(caja.idCaja, request).subscribe({
      next: () => {
        this.mensaje.set(`Estado de la caja "${caja.nombre}" cambiado a ${this.cajaService.etiquetaEstado(nuevoEstado)}`);
        this.cargarCajas();
      },
      error: (err) => this.error.set(this.extraerError(err))
    });
  }

  tieneFondos(caja: Caja): boolean {
    return caja.montoActual > 0;
  }

  esEstadoInvalido(caja: Caja): boolean {
    // Deshabilitar el select si la caja no tiene fondos y el usuario intenta poner ACTIVA
    // Esto se evalúa en el momento del cambio, no en el disabled estático
    // Para el disabled estático, solo deshabilitamos si no tiene fondos
    return !this.tieneFondos(caja);
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}
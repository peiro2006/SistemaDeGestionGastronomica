import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Caja } from '../../models/caja.models';
import { CajasService } from '../../services/cajas.service';

@Component({
  selector: 'app-admin-cajas',
  imports: [ReactiveFormsModule, RouterLink, DatePipe, DecimalPipe],
  templateUrl: './admin-cajas.html',
  styleUrl: './admin-cajas.css'
})
export class AdminCajasComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cajasService = inject(CajasService);

  readonly cajas = signal<Caja[]>([]);
  readonly cargando = signal(false);
  readonly guardando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombreCaja: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    montoInicial: [0, [Validators.required, Validators.min(0)]],
    moneda: ['ARS', [Validators.required]],
    descripcionCaja: ['', [Validators.maxLength(500)]],
    activa: [true, [Validators.required]]
  });

  ngOnInit(): void {
    this.cargarCajas();
  }

  cajasActivas(): number {
    return this.cajas().filter((caja) => caja.activa).length;
  }

  montoTotal(): number {
    return this.cajas().reduce((total, caja) => total + (caja.montoInicial ?? 0), 0);
  }

  cargarCajas(): void {
    this.cargando.set(true);
    this.cajasService.listar().subscribe({
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

  guardarCaja(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    const value = this.form.getRawValue();
    this.cajasService
      .crear({
        nombreCaja: value.nombreCaja.trim(),
        montoInicial: value.montoInicial,
        moneda: value.moneda,
        descripcionCaja: value.descripcionCaja.trim() || null,
        activa: value.activa
      })
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.mensaje.set('Caja creada correctamente');
          this.form.reset({ nombreCaja: '', montoInicial: 0, moneda: 'ARS', descripcionCaja: '', activa: true });
          this.cargarCajas();
        },
        error: (err) => {
          this.guardando.set(false);
          this.error.set(this.extraerError(err));
        }
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
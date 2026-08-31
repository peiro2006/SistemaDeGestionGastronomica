import { Component, inject, signal, OnInit, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CajaService } from '../../services/caja.service';
import { CajaSelectorService } from '../../services/caja-selector.service';
import { Caja } from '../../models/caja.models';

@Component({
  selector: 'app-caja-selector',
  imports: [CommonModule, FormsModule],
  templateUrl: './caja-selector.html',
  styleUrl: './caja-selector.css'
})
export class CajaSelectorComponent implements OnInit {
  protected readonly cajaService = inject(CajaService);
  private readonly selectorService = inject(CajaSelectorService);
  private readonly router = inject(Router);

  readonly cajas = signal<Caja[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly procesando = signal<{ id: number; loading: boolean } | null>(null);

  readonly form = signal<{ idCaja: number | null; password: string }>({
    idCaja: null,
    password: ''
  });

  readonly mostrar = this.selectorService.mostrar;

  ngOnInit(): void {
    // Cargar cuando se abre - usar effect
    effect(() => {
      if (this.mostrar()) {
        this.cargarCajasDisponibles();
      }
    });
  }

  cerrar(): void {
    this.selectorService.cerrar();
    this.form.set({ idCaja: null, password: '' });
    this.error.set(null);
  }

  tieneFondos(caja: Caja): boolean {
    return caja.montoActual > 0;
  }

  cargarCajasDisponibles(): void {
    this.cargando.set(true);
    this.cajaService.listarDisponibles().subscribe({
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

  seleccionarCaja(idCaja: number): void {
    this.form.update(f => ({ ...f, idCaja, password: '' }));
  }

  abrirCaja(): void {
    const { idCaja, password } = this.form();
    if (!idCaja || !password) return;

    this.procesando.set({ id: idCaja, loading: true });
    this.error.set(null);

    this.cajaService.loginCaja({ idCaja, password }).subscribe({
      next: (res) => {
        this.procesando.set(null);
        // Guardar info de caja en localStorage/sessionStorage
        if (typeof window !== 'undefined') {
          sessionStorage.setItem('caja_actual', JSON.stringify({
            idCaja: res.data.idCaja,
            nombre: res.data.nombre,
            montoActual: res.data.montoActual,
            token: res.data.token
          }));
        }
        this.router.navigate(['/empleado/pedidos']);
      },
      error: (err) => {
        this.procesando.set(null);
        this.error.set(this.extraerError(err));
      }
    });
  }

  irAdminCajas(): void {
    this.router.navigate(['/admin/cajas']);
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo abrir la caja.';
  }
}
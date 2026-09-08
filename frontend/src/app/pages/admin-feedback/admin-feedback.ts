import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Resena } from '../../services/resenas.service';
import { ResenasService } from '../../services/resenas.service';

@Component({
  selector: 'app-admin-feedback',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './admin-feedback.html',
  styleUrl: './admin-feedback.css'
})
export class AdminFeedbackComponent implements OnInit {
  private readonly resenasService = inject(ResenasService);

  readonly resenas = signal<Resena[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  readonly filtroProducto = signal('');
  readonly filtroFecha = signal('');
  readonly filtroUsuario = signal('');

  ngOnInit(): void {
    this.cargarResenas();
  }

  cargarResenas(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.resenasService.listarTodas().subscribe({
      next: (res) => {
        this.resenas.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  aplicarFiltros(): void {
    const producto = this.filtroProducto() || undefined;
    const fecha = this.filtroFecha() || undefined;
    const usuario = this.filtroUsuario() || undefined;

    if (!producto && !fecha && !usuario) {
      this.cargarResenas();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);
    this.resenasService.listarConFiltros({ producto, fecha, usuario }).subscribe({
      next: (res) => {
        this.resenas.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  limpiarFiltros(): void {
    this.filtroProducto.set('');
    this.filtroFecha.set('');
    this.filtroUsuario.set('');
    this.cargarResenas();
  }

  estrellasCompletas(calificacion: number): number {
    return Math.floor(calificacion);
  }

  tieneMediaEstrella(calificacion: number): boolean {
    return calificacion % 1 !== 0;
  }

  estrellasVacias(calificacion: number): number {
    return 5 - Math.ceil(calificacion);
  }

  promedioCalificacion(): string {
    if (this.resenas().length === 0) return '0.0';
    const suma = this.resenas().reduce((acc, r) => acc + r.calificacion, 0);
    return (suma / this.resenas().length).toFixed(1);
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

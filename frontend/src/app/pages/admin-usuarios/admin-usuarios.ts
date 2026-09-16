import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';
import { Usuario } from '../../models/auth.models';
import { UsuariosAdminService } from '../../services/usuarios-admin.service';

@Component({
  selector: 'app-admin-usuarios',
  imports: [FormsModule, DatePipe, AdminSidebarComponent],
  templateUrl: './admin-usuarios.html',
  styleUrl: './admin-usuarios.css'
})
export class AdminUsuariosComponent {
  private readonly usuariosService = inject(UsuariosAdminService);

  readonly emailBusqueda = signal('');
  readonly usuarioEncontrado = signal<Usuario | null>(null);
  readonly cargando = signal(false);
  readonly procesando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  buscar(): void {
    const email = this.emailBusqueda().trim();
    if (!email) {
      this.error.set('Ingrese un email para buscar.');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);
    this.mensaje.set(null);
    this.usuarioEncontrado.set(null);

    this.usuariosService.buscarPorEmail(email).subscribe({
      next: (res) => {
        this.usuarioEncontrado.set(res.data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(this.extraerError(err));
      }
    });
  }

  promover(): void {
    const usuario = this.usuarioEncontrado();
    if (!usuario) return;

    this.procesando.set(true);
    this.error.set(null);

    this.usuariosService.promover(usuario.email).subscribe({
      next: (res) => {
        this.usuarioEncontrado.set(res.data);
        this.mensaje.set(`${usuario.nombre} ${usuario.apellido} fue promovido a empleado correctamente`);
        this.procesando.set(false);
      },
      error: (err) => {
        this.procesando.set(false);
        this.error.set(this.extraerError(err));
      }
    });
  }

  desemplar(): void {
    const usuario = this.usuarioEncontrado();
    if (!usuario) return;

    this.procesando.set(true);
    this.error.set(null);

    this.usuariosService.desemplar(usuario.email).subscribe({
      next: (res) => {
        this.usuarioEncontrado.set(res.data);
        this.mensaje.set(`Se quitó el rol de empleado a ${usuario.nombre} ${usuario.apellido}`);
        this.procesando.set(false);
      },
      error: (err) => {
        this.procesando.set(false);
        this.error.set(this.extraerError(err));
      }
    });
  }

  limpiar(): void {
    this.emailBusqueda.set('');
    this.usuarioEncontrado.set(null);
    this.error.set(null);
    this.mensaje.set(null);
  }

  rolEtiqueta(rol: string): string {
    switch (rol) {
      case 'ROLE_ADMIN': return 'Administrador';
      case 'ROLE_EMPLEADO': return 'Empleado';
      case 'ROLE_USER': return 'Usuario';
      default: return rol;
    }
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

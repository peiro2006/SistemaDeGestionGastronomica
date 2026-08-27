import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  errorMessage: string | null = null;
  loading = false;

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    const credenciales = {
      email: this.form.value.email!,
      password: this.form.value.password!
    };

    this.authService.login(credenciales).subscribe({
      next: () => {
        const rol = this.authService.currentUser()?.rol;
        let destino = '/catalogo';
        if (rol === 'ROLE_ADMIN') {
          destino = '/admin/productos';
        } else if (rol === 'ROLE_EMPLEADO') {
          destino = '/empleado/pedidos';
        }
        this.router.navigate([destino]);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.extraerError(err);
      }
    });
  }

  private extraerError(err: HttpErrorResponse): string {
    // Errores del backend (cuerpo de respuesta)
    if (err.error?.message) {
      const msg = err.error.message.toLowerCase();
      if (msg.includes('credenciales invalidas') || msg.includes('unauthorized') || msg.status === 401) {
        return 'Email o contraseña incorrectos. Por favor, intente nuevamente.';
      }
      if (msg.includes('usuario no encontrado') || msg.includes('not found')) {
        return 'No existe una cuenta con ese email. Regístrese primero.';
      }
      if (msg.includes('bad credentials')) {
        return 'Email o contraseña incorrectos.';
      }
      return err.error.message;
    }

    // Errores de validación del backend
    if (err.error?.errors?.length) {
      return err.error.errors.join(' ');
    }

    // Errores de red
    if (err.status === 0) {
      return 'No se puede conectar con el servidor. Verifique que el backend esté corriendo en http://localhost:8080';
    }

    // Error genérico según código de estado
    switch (err.status) {
      case 400: return 'Datos inválidos. Verifique la información ingresada.';
      case 401: return 'Email o contraseña incorrectos.';
      case 403: return 'No tiene permisos para acceder.';
      case 404: return 'Servicio no encontrado.';
      case 500: return 'Error interno del servidor. Intente más tarde.';
      default: return 'Error al iniciar sesión. Intente nuevamente más tarde.';
    }
  }
}
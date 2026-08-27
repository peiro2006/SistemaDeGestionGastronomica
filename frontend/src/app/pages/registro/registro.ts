import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

function passwordsCoinciden(control: AbstractControl): { [key: string]: boolean } | null {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');
  if (password && confirmPassword && password.value !== confirmPassword.value) {
    return { passwordsCoinciden: true };
  }
  return null;
}

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class RegistroComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.group(
    {
      nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      apellido: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/)
        ]
      ],
      confirmPassword: ['', [Validators.required]]
    },
    { validators: passwordsCoinciden }
  );

  errorMessage: string | null = null;
  successMessage: string | null = null;
  loading = false;

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const datos = {
      nombre: this.form.value.nombre!,
      apellido: this.form.value.apellido!,
      email: this.form.value.email!,
      password: this.form.value.password!
    };

    this.authService.registrar(datos).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '¡Cuenta creada correctamente! Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.extraerError(err);
      }
    });
  }

  private extraerError(err: HttpErrorResponse): string {
    // Errores de validación del backend
    if (err.error?.errors?.length) {
      return err.error.errors.join(' ');
    }

    // Mensaje de error del backend
    if (err.error?.message) {
      const msg = err.error.message.toLowerCase();
      if (msg.includes('ya existe') || msg.includes('duplicate') || msg.includes('conflict') || err.status === 409) {
        return 'Ya existe una cuenta con ese email. Intente iniciar sesión o use otro email.';
      }
      if (msg.includes('valid') || msg.includes('pattern') || msg.includes('constraint')) {
        return 'Los datos ingresados no son válidos. Verifique el formato del email y la contraseña.';
      }
      return err.error.message;
    }

    // Errores de validación del frontend (Angular)
    if (this.form.errors?.['passwordsCoinciden']) {
      return 'Las contraseñas no coinciden.';
    }
    if (this.form.get('email')?.errors?.['email']) {
      return 'El formato del email no es válido.';
    }
    if (this.form.get('password')?.errors?.['pattern']) {
      return 'La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.';
    }
    if (this.form.get('password')?.errors?.['minlength']) {
      return 'La contraseña debe tener al menos 8 caracteres.';
    }

    // Errores de red
    if (err.status === 0) {
      return 'No se puede conectar con el servidor. Verifique que el backend esté corriendo en http://localhost:8080';
    }

    // Error genérico según código de estado
    switch (err.status) {
      case 400: return 'Datos inválidos. Verifique la información ingresada.';
      case 409: return 'Ya existe una cuenta con ese email. Intente iniciar sesión o use otro email.';
      case 500: return 'Error interno del servidor. Intente más tarde.';
      default: return 'Error al crear la cuenta. Intente nuevamente más tarde.';
    }
  }
}
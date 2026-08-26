import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
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
        const destino = this.authService.currentUser()?.rol === 'ROLE_ADMIN' ? '/admin/productos' : '/catalogo';
        this.router.navigate([destino]);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.extraerError(err);
      }
    });
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.message) {
      return httpError.error.message;
    }
    return 'No se pudo iniciar sesion. Verifique sus credenciales.';
  }
}

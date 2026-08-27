import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const empleadoGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const rol = authService.currentUser()?.rol;
  if (rol === 'ROLE_EMPLEADO' || rol === 'ROLE_ADMIN') {
    return true;
  }

  return router.createUrlTree(['/catalogo']);
};

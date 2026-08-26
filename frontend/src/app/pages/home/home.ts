import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { BaseResponse } from '../../models/auth.models';

interface Producto {
  idProducto: number;
  nombreProducto: string;
  descripcion: string;
  precio: string;
  idReceta: number;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);

  readonly usuario = this.authService.currentUser;
  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(false);
  readonly mensajeApi = signal<string | null>(null);

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  cargarProductos(): void {
    this.cargando.set(true);
    this.mensajeApi.set(null);

    this.http
      .get<BaseResponse<Producto[]>>('http://localhost:8080/Producto')
      .subscribe({
        next: (res) => {
          this.cargando.set(false);
          this.productos.set(res.data ?? []);
        },
        error: () => {
          this.cargando.set(false);
          this.mensajeApi.set('No se pudieron obtener los datos protegidos.');
        }
      });
  }
}

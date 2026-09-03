import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Producto } from '../../models/producto.models';
import { AuthService } from '../../services/auth.service';
import { ProductosService } from '../../services/productos.service';
import { CarritoService } from '../../services/carrito.service';

@Component({
  selector: 'app-catalogo',
  imports: [FormsModule, RouterLink],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css'
})
export class CatalogoComponent implements OnInit {
  private readonly productosService = inject(ProductosService);
  private readonly authService = inject(AuthService);
  private readonly carritoService = inject(CarritoService);
  private readonly router = inject(Router);

  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly textoBusqueda = signal('');
  readonly categoriaFiltro = signal('');

  readonly usuario = this.authService.currentUser;

  ngOnInit(): void {
    this.cargarCatalogo();
  }

  cargarCatalogo(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.productosService
      .catalogo({
        nombre: this.textoBusqueda() || undefined,
        categoria: this.categoriaFiltro() || undefined
      })
      .subscribe({
        next: (res) => {
          this.productos.set(res.data ?? []);
          this.cargando.set(false);
        },
        error: (err) => {
          this.error.set(this.extraerError(err));
          this.cargando.set(false);
        }
      });
  }

  buscar(): void {
    this.cargarCatalogo();
  }

  limpiarFiltros(): void {
    this.textoBusqueda.set('');
    this.categoriaFiltro.set('');
    this.cargarCatalogo();
  }

  agregarAlCarrito(producto: Producto, event: Event): void {
    event.stopPropagation();
    const input = (event.target as HTMLElement).closest('.card-actions')?.querySelector('input') as HTMLInputElement | null;
    const cantidad = input ? Number(input.value) || 1 : 1;

    if (!this.carritoService.agregar(producto, cantidad)) {
      const stock = producto.stockActual ?? 0;
      this.error.set(`Stock insuficiente para ${producto.nombreProducto}. Disponible: ${stock}`);
      return;
    }
    this.mensaje.set(`${producto.nombreProducto} agregado al carrito`);
  }

  irALogin(): void {
    this.router.navigate(['/login']);
  }

  private extraerError(err: unknown): string {
    const httpError = err as { error?: { message?: string; errors?: string[] | null } };
    if (httpError?.error?.errors?.length) {
      return httpError.error.errors.join(' - ');
    }
    return httpError?.error?.message ?? 'No se pudo completar la operacion.';
  }
}

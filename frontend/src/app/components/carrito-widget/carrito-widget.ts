import { Component, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { CarritoService, CarritoItem } from '../../services/carrito.service';
import { AuthService } from '../../services/auth.service';
import { PedidosService } from '../../services/pedidos.service';
import { MetodoPago } from '../../models/pedido.models';

@Component({
  selector: 'app-carrito-widget',
  templateUrl: './carrito-widget.html',
  styleUrl: './carrito-widget.css'
})
export class CarritoWidgetComponent {
  private readonly carritoService = inject(CarritoService);
  private readonly authService = inject(AuthService);
  private readonly pedidosService = inject(PedidosService);
  private readonly router = inject(Router);

  readonly carrito = this.carritoService.items;
  readonly total = this.carritoService.total;
  readonly metodoPago = this.carritoService.metodoPago;
  readonly usuario = this.authService.currentUser;

  readonly mostrar = signal(false);

  readonly metodosPago: { valor: MetodoPago; label: string; icono: string }[] = [
    { valor: 'EFECTIVO', label: 'Efectivo', icono: '$' },
    { valor: 'DEBITO', label: 'Debito', icono: 'D' },
    { valor: 'TARJETA_CREDITO', label: 'Credito', icono: 'C' },
    { valor: 'TRANSFERENCIA', label: 'Transferencia', icono: 'T' }
  ];

  estaLogueado(): boolean {
    return this.authService.isAuthenticated();
  }
  readonly procesando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly carritoCount = computed(() =>
    this.carrito().reduce((sum, item) => sum + item.cantidad, 0)
  );

  toggle(): void {
    this.mostrar.update((v) => !v);
  }

  cerrar(): void {
    this.mostrar.set(false);
  }

  incrementar(idProducto: number): void {
    this.carritoService.incrementar(idProducto);
  }

  decrementar(idProducto: number): void {
    this.carritoService.decrementar(idProducto);
  }

  quitar(idProducto: number): void {
    this.carritoService.quitar(idProducto);
  }

  calcularSubtotal(precio: string, cantidad: number): string {
    return (Number(precio) * cantidad).toFixed(2);
  }

  seleccionarMetodoPago(metodo: MetodoPago): void {
    this.carritoService.setMetodoPago(metodo);
  }

  checkout(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    const items = this.carrito();
    if (!items.length) {
      return;
    }

    this.procesando.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    this.pedidosService
      .crear({
        items: items.map((item) => ({
          idProducto: item.producto.idProducto,
          cantidad: item.cantidad
        })),
        metDePago: this.metodoPago()
      })
      .subscribe({
        next: (res) => {
          this.procesando.set(false);
          this.carritoService.limpiar();
          this.mensaje.set(`Pedido #${res.data.idPedido} creado correctamente. Total: $${res.data.total}`);
        },
        error: (err) => {
          this.procesando.set(false);
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

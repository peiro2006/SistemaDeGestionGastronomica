import { Component, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { CarritoService } from '../../services/carrito.service';
import { AuthService } from '../../services/auth.service';
import { PedidosService } from '../../services/pedidos.service';
import { MetodoPago } from '../../models/pedido.models';

interface OpcionMetodoPago {
  valor: MetodoPago;
  label: string;
  icono: string;
}

interface DatosTarjeta {
  numero: string;
  nombre: string;
  vencimiento: string;
  cvv: string;
}

type CampoTarjeta = 'numero' | 'nombre' | 'vencimiento' | 'cvv';

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
  readonly estaLogueado = this.authService.isAuthenticated;

  readonly mostrar = signal(false);
  readonly procesando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly metodosPago: OpcionMetodoPago[] = [
    { valor: 'EFECTIVO', label: 'Efectivo', icono: 'E' },
    { valor: 'DEBITO', label: 'Debito', icono: 'D' },
    { valor: 'TARJETA_CREDITO', label: 'Credito', icono: 'C' }
  ];

  // Para debito se puede pagar escaneando un QR o cargando los datos de la tarjeta
  readonly modoDebito = signal<'qr' | 'tarjeta'>('qr');

  // Codigo de pago simulado (genera un QR determinista a partir de el)
  readonly codigoPago = signal(this.generarCodigoPago());
  readonly matrizQr = computed(() => this.generarMatrizQr(this.codigoPago()));

  readonly tarjeta = signal<DatosTarjeta>({
    numero: '',
    nombre: '',
    vencimiento: '',
    cvv: ''
  });

  readonly tarjetaValida = computed(() => this.esTarjetaValida());

  readonly puedeConfirmar = computed(() => {
    const metodo = this.metodoPago();
    if (metodo === 'EFECTIVO') {
      return true;
    }
    if (metodo === 'DEBITO') {
      return this.modoDebito() === 'qr' || this.esTarjetaValida();
    }
    if (metodo === 'TARJETA_CREDITO') {
      return this.esTarjetaValida();
    }
    return false;
  });

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

  seleccionarModoDebito(modo: 'qr' | 'tarjeta'): void {
    this.modoDebito.set(modo);
  }

  actualizarCampo(campo: CampoTarjeta, evento: Event): void {
    const valor = (evento.target as HTMLInputElement).value;
    this.tarjeta.update((t) => ({ ...t, [campo]: this.formatearCampo(campo, valor) }));
  }

  private formatearCampo(campo: CampoTarjeta, valor: string): string {
    if (campo === 'numero') {
      return this.formatoNumeroTarjeta(valor);
    }
    if (campo === 'vencimiento') {
      return this.formatoVencimiento(valor);
    }
    if (campo === 'cvv') {
      return valor.replace(/\D/g, '').slice(0, 4);
    }
    return valor.slice(0, 50);
  }

  private formatoNumeroTarjeta(valor: string): string {
    const digitos = valor.replace(/\D/g, '').slice(0, 16);
    return digitos.replace(/(\d{4})(?=\d)/g, '$1 ');
  }

  private formatoVencimiento(valor: string): string {
    const digitos = valor.replace(/\D/g, '').slice(0, 4);
    return digitos.length > 2 ? digitos.slice(0, 2) + '/' + digitos.slice(2) : digitos;
  }

  private soloDigitos(valor: string): string {
    return (valor ?? '').replace(/\D/g, '');
  }

  private numeroValido(numero: string): boolean {
    return this.soloDigitos(numero).length === 16;
  }

  private nombreValido(nombre: string): boolean {
    const n = (nombre ?? '').trim();
    return n.length >= 3 && /^[a-zA-Z\sáéíóúÁÉÍÓÚñÑ]+$/.test(n);
  }

  private vencimientoValido(vencimiento: string): boolean {
    const match = (vencimiento ?? '').trim().match(/^(0[1-9]|1[0-2])\/(\d{2})$/);
    if (!match) {
      return false;
    }
    const mes = Number(match[1]);
    const anio = 2000 + Number(match[2]);
    const ahora = new Date();
    const vencimientoYm = anio * 100 + mes;
    const ahoraYm = ahora.getFullYear() * 100 + (ahora.getMonth() + 1);
    return vencimientoYm >= ahoraYm;
  }

  private cvvValido(cvv: string): boolean {
    return /^\d{3,4}$/.test((cvv ?? '').trim());
  }

  private esTarjetaValida(): boolean {
    const t = this.tarjeta();
    return (
      this.numeroValido(t.numero) &&
      this.nombreValido(t.nombre) &&
      this.vencimientoValido(t.vencimiento) &&
      this.cvvValido(t.cvv)
    );
  }

  campoTarjetaInvalido(campo: CampoTarjeta): boolean {
    const valor = this.tarjeta()[campo];
    if (!valor) {
      return false;
    }
    switch (campo) {
      case 'numero': return !this.numeroValido(valor);
      case 'nombre': return !this.nombreValido(valor);
      case 'vencimiento': return !this.vencimientoValido(valor);
      case 'cvv': return !this.cvvValido(valor);
    }
  }

  private generarCodigoPago(): string {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ0123456789';
    let resultado = '';
    for (let i = 0; i < 8; i++) {
      resultado += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return 'HB-' + resultado;
  }

  private generarMatrizQr(codigo: string): number[][] {
    let seed = 2166136261;
    for (let i = 0; i < codigo.length; i++) {
      seed ^= codigo.charCodeAt(i);
      seed = Math.imul(seed, 16777619) >>> 0;
    }
    let state = seed || 1;
    const rnd = () => {
      state = (Math.imul(state, 1664525) + 1013904223) >>> 0;
      return state / 4294967296;
    };

    const size = 21;
    const modulo = Array.from({ length: size }, () =>
      Array.from({ length: size }, () => (rnd() > 0.5 ? 1 : 0))
    );

    const marcador = (r0: number, c0: number) => {
      for (let r = 0; r < 7; r++) {
        for (let c = 0; c < 7; c++) {
          const borde = r === 0 || r === 6 || c === 0 || c === 6;
          const nucleo = r >= 2 && r <= 4 && c >= 2 && c <= 4;
          modulo[r0 + r][c0 + c] = borde || nucleo ? 1 : 0;
        }
      }
    };

    marcador(0, 0);
    marcador(0, size - 7);
    marcador(size - 7, 0);

    return modulo;
  }

  formatearVencimiento(): void {
    /* mantiene el formato aunque el foco salga de un campo */
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

    if (!this.puedeConfirmar()) {
      this.error.set('Completa los datos de pago antes de confirmar el pedido.');
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

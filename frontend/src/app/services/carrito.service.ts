import { Injectable, computed, signal } from '@angular/core';
import { Producto } from '../models/producto.models';

export interface CarritoItem {
  producto: Producto;
  cantidad: number;
}

@Injectable({ providedIn: 'root' })
export class CarritoService {
  readonly items = signal<CarritoItem[]>([]);
  readonly total = computed(() =>
    this.items().reduce((total, item) => total + Number(item.producto.precio) * item.cantidad, 0)
  );

  agregar(producto: Producto, cantidad: number): boolean {
    const stockDisponible = producto.stockActual ?? 0;
    const cantidadSegura = Math.max(1, cantidad);
    const actual = this.items().find((item) => item.producto.idProducto === producto.idProducto)?.cantidad ?? 0;

    if (actual + cantidadSegura > stockDisponible) {
      return false;
    }

    this.items.update((items) => {
      const existente = items.find((item) => item.producto.idProducto === producto.idProducto);
      if (!existente) {
        return [...items, { producto, cantidad: cantidadSegura }];
      }
      return items.map((item) =>
        item.producto.idProducto === producto.idProducto
          ? { ...item, cantidad: item.cantidad + cantidadSegura }
          : item
      );
    });
    return true;
  }

  quitar(idProducto: number): void {
    this.items.update((items) => items.filter((item) => item.producto.idProducto !== idProducto));
  }

  limpiar(): void {
    this.items.set([]);
  }
}

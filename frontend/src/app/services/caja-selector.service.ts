import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CajaSelectorService {
  readonly mostrar = signal(false);

  abrir(): void {
    this.mostrar.set(true);
  }

  cerrar(): void {
    this.mostrar.set(false);
  }

  toggle(): void {
    this.mostrar.update(v => !v);
  }
}
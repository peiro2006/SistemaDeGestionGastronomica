import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar';
import { Proveedor } from '../../models/proveedor.models';
import { ProveedoresService } from '../../services/proveedores.service';

@Component({
  selector: 'app-admin-proveedores',
  imports: [ReactiveFormsModule, RouterLink, RouterLinkActive, DatePipe, AdminSidebarComponent],
  templateUrl: './admin-proveedores.html',
  styleUrl: './admin-proveedores.css'
})
export class AdminProveedoresComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly proveedoresService = inject(ProveedoresService);

  readonly proveedores = signal<Proveedor[]>([]);
  readonly proveedorEditando = signal<Proveedor | null>(null);
  readonly cargando = signal(false);
  readonly guardando = signal(false);
  readonly mensaje = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
    telefono: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    direccion: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
    ciudad: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]]
  });

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores(): void {
    this.cargando.set(true);
    this.proveedoresService.listarTodas().subscribe({
      next: (res) => {
        this.proveedores.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(this.extraerError(err));
        this.cargando.set(false);
      }
    });
  }

  guardarProveedor(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.error.set(null);
    this.mensaje.set(null);

    const value = this.form.getRawValue();
    const editando = this.proveedorEditando();

    const datos = {
      nombre: value.nombre.trim(),
      telefono: value.telefono.trim(),
      email: value.email.trim(),
      direccion: value.direccion.trim(),
      ciudad: value.ciudad.trim()
    };

    const accion = editando
      ? this.proveedoresService.actualizar(editando.idProveedor, datos)
      : this.proveedoresService.crear(datos);

    accion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mensaje.set(editando ? 'Proveedor actualizado correctamente' : 'Proveedor creado correctamente');
        this.cancelarEdicion();
        this.cargarProveedores();
      },
      error: (err) => {
        this.guardando.set(false);
        this.error.set(this.extraerError(err));
      }
    });
  }

  editar(proveedor: Proveedor): void {
    this.proveedorEditando.set(proveedor);
    this.form.patchValue({
      nombre: proveedor.nombre,
      telefono: proveedor.telefono,
      email: proveedor.email,
      direccion: proveedor.direccion,
      ciudad: proveedor.ciudad
    });
    this.mensaje.set(null);
    this.error.set(null);
  }

  cancelarEdicion(): void {
    this.proveedorEditando.set(null);
    this.form.reset({ nombre: '', telefono: '', email: '', direccion: '', ciudad: '' });
  }

  eliminarProveedor(proveedor: Proveedor): void {
    const confirmacion = window.confirm(
      `¿Seguro que desea eliminar el proveedor "${proveedor.nombre}"?`
    );
    if (!confirmacion) return;

    this.error.set(null);
    this.mensaje.set(null);
    this.proveedoresService.actualizar(proveedor.idProveedor, { activo: false }).subscribe({
      next: () => {
        this.mensaje.set('Proveedor desactivado correctamente');
        this.cargarProveedores();
      },
      error: (err) => this.error.set(this.extraerError(err))
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
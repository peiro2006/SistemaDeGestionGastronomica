import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Proveedor } from '../../models/proveedor.models';
import { ProveedoresService } from '../../services/proveedores.service';

@Component({
  selector: 'app-admin-proveedores',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
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
    razonSocial: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    cuitRut: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(15), Validators.pattern(/^[0-9]+$/)]],
    telefono: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20), Validators.pattern(/^[0-9+\-\s()]+$/)]],
    correo: ['', [Validators.required, Validators.email]],
    direccion: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(200)]]
  });

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores(): void {
    this.cargando.set(true);
    this.proveedoresService.listar().subscribe({
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
      razonSocial: value.razonSocial.trim(),
      cuitRut: value.cuitRut.trim(),
      telefono: value.telefono.trim(),
      correo: value.correo.trim(),
      direccion: value.direccion.trim()
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
      razonSocial: proveedor.razonSocial,
      cuitRut: proveedor.cuitRut,
      telefono: proveedor.telefono,
      correo: proveedor.correo,
      direccion: proveedor.direccion
    });
    this.mensaje.set(null);
    this.error.set(null);
  }

  cancelarEdicion(): void {
    this.proveedorEditando.set(null);
    this.form.reset({ razonSocial: '', cuitRut: '', telefono: '', correo: '', direccion: '' });
  }

  eliminarProveedor(proveedor: Proveedor): void {
    const confirmacion = window.confirm(
      `¿Seguro que desea eliminar el proveedor "${proveedor.razonSocial}" (CUIT/RUT ${proveedor.cuitRut})?`
    );
    if (!confirmacion) {
      return;
    }

    this.error.set(null);
    this.mensaje.set(null);
    this.proveedoresService.eliminar(proveedor.idProveedor).subscribe({
      next: () => {
        this.mensaje.set('Proveedor eliminado correctamente');
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
export interface Proveedor {
  idProveedor: number;
  nombre: string;
  telefono: string;
  email: string;
  direccion: string;
  ciudad: string;
  activo: boolean;
  fechaCreacion: string;
}

export interface ProveedorCreateRequest {
  nombre: string;
  telefono: string;
  email: string;
  direccion: string;
  ciudad: string;
}

export interface ProveedorUpdateRequest {
  nombre?: string;
  telefono?: string;
  email?: string;
  direccion?: string;
  ciudad?: string;
  activo?: boolean;
}
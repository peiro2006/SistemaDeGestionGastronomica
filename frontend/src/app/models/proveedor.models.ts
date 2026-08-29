export interface Proveedor {
  idProveedor: number;
  razonSocial: string;
  cuitRut: string;
  telefono: string;
  correo: string;
  direccion: string;
  fechaCreacion: string;
  fechaUltimaModificacion: string | null;
  usuarioAlta: string | null;
  usuarioUltimaModificacion: string | null;
}

export interface ProveedorCreateRequest {
  razonSocial: string;
  cuitRut: string;
  telefono: string;
  correo: string;
  direccion: string;
}

export type ProveedorUpdateRequest = ProveedorCreateRequest;
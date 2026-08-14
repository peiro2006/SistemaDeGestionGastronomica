export interface BaseResponse<T> {
  data: T;
  message: string;
  errors: string[] | null;
  timestamp: string;
}

export interface UsuarioRegistroRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface Usuario {
  idUsuario: number;
  nombre: string;
  apellido: string;
  email: string;
  rol: string;
  fechaCreacion: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  usuario: Usuario;
}

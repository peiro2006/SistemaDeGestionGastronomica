import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import {
  BaseResponse,
  LoginRequest,
  LoginResponse,
  Usuario,
  UsuarioRegistroRequest
} from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080';
  private readonly tokenKey = 'auth_token';
  private readonly userKey = 'auth_user';

  readonly currentUser = signal<Usuario | null>(this.getStoredUser());

  constructor(private http: HttpClient) {}

  registrar(datos: UsuarioRegistroRequest): Observable<BaseResponse<Usuario>> {
    return this.http.post<BaseResponse<Usuario>>(
      `${this.apiUrl}/auth/registro`,
      datos
    );
  }

  login(credenciales: LoginRequest): Observable<BaseResponse<LoginResponse>> {
    return this.http
      .post<BaseResponse<LoginResponse>>(`${this.apiUrl}/auth/login`, credenciales)
      .pipe(tap((res) => this.guardarSesion(res.data)));
  }

  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
    this.currentUser.set(null);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  getToken(): string | null {
    if (typeof window === 'undefined') {
      return null;
    }
    return localStorage.getItem(this.tokenKey);
  }

  private guardarSesion(data: LoginResponse): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.tokenKey, data.token);
      localStorage.setItem(this.userKey, JSON.stringify(data.usuario));
    }
    this.currentUser.set(data.usuario);
  }

  private getStoredUser(): Usuario | null {
    if (typeof window === 'undefined') {
      return null;
    }
    const raw = localStorage.getItem(this.userKey);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as Usuario;
    } catch {
      return null;
    }
  }
}

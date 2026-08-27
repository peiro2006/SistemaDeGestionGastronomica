import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
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
  private readonly platformId = inject(PLATFORM_ID);

  // Signal que se inicializa de forma lazy para evitar problemas de hidratación SSR
  private _currentUser = signal<Usuario | null>(null);
  readonly currentUser = computed(() => this._currentUser());

  constructor(private http: HttpClient) {
    // Inicializar solo en el navegador, después de la hidratación
    if (isPlatformBrowser(this.platformId)) {
      const stored = this.getStoredUser();
      if (stored) {
        this._currentUser.set(stored);
      }
    }
  }

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
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
    this._currentUser.set(null);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }
    return localStorage.getItem(this.tokenKey);
  }

  private guardarSesion(data: LoginResponse): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.tokenKey, data.token);
      localStorage.setItem(this.userKey, JSON.stringify(data.usuario));
    }
    this._currentUser.set(data.usuario);
  }

  private getStoredUser(): Usuario | null {
    if (!isPlatformBrowser(this.platformId)) {
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
import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = '/api/auth';

  private currentUserSig = signal<AuthResponse['user'] | null>(null);

  currentUser = computed(() => this.currentUserSig());
  isLoggedIn = computed(() => !!this.currentUserSig());

  constructor() {
    const token = localStorage.getItem('token');
    const userStored = localStorage.getItem('user');

    if (token && userStored) {
      try {
        this.currentUserSig.set(JSON.parse(userStored));
      } catch (error) {
        console.error('Error al parsear usuario:', error);
        this.currentUserSig.set(null);
      }
    } else {
        this.currentUserSig.set(null);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}`, credentials).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));

        this.currentUserSig.set(response.user);
      })
    );
  }

  register(datos: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, datos);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSig.set(null);
    this.router.navigate(['/home']);
  }
}

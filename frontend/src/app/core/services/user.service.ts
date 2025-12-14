import { Injectable, signal, computed, inject, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = '/api/users';

  private currentUserSig = signal<UserProfile | null>(null);

  currentUser = computed(() => this.currentUserSig());

  constructor() {
    const userStored = localStorage.getItem('user');

    if (userStored) {
      try {
        this.currentUserSig.set(JSON.parse(userStored));
      } catch (error) {
        console.error('Error al parsear usuario:', error);
        this.currentUserSig.set(null);
      }
    } else {
      this.currentUserSig.set(null);
    }

    // Cuando el usuario se loguea, cargar el perfil automáticamente
    effect(() => {
      const isLoggedIn = this.authService.isLoggedIn();
      const currentAuthUser = this.authService.currentUser();

      if (isLoggedIn && currentAuthUser && !this.currentUserSig()) {
        this.getUserProfile().subscribe({
          error: (error) => {
            console.error('Error al cargar perfil automáticamente:', error);
          }
        });
      } else if (!isLoggedIn) {
        this.currentUserSig.set(null);
      }
    });
  }


  getUserProfile(): Observable<UserProfile> {
    const userId = this.authService.currentUser()?.id;

    if (!userId) {
      throw new Error('No hay usuario autenticado');
    }

    return this.http.get<UserProfile>(`${this.apiUrl}/${userId}`).pipe(
      tap((user) => {
        this.currentUserSig.set(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  updateUserProfile(userData: UserUpdateRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/update`, userData).pipe(
      tap((userActualizado) => {
        this.currentUserSig.set(userActualizado);
        localStorage.setItem('user', JSON.stringify(userActualizado));
      })
    );
  }

  getAllUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(this.apiUrl);
  }

  updateUserStatus(userId: number, enabled: boolean): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/admin/${userId}/status?enabled=${enabled}`, {});
  }

  adminUpdateUser(userId: number, userData: AdminUserUpdateRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/admin/${userId}`, userData);
  }

}

import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthService } from './auth.service';

export interface UserProfile {
  id: number;
  name: string;
  lastName: string;
  email: string;
  phone: string;
  city: string;
  neighborhood: string;
  latitude: number;
  longitude: number;
  points: number;
}

export interface UserUpdateDTO {
  name?: string;
  lastName?: string;
  phoneNumber?: string;
  city?: string;
  neighborhood?: string;
  latitude?: number;
  longitude?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = '/api/users';

  private currentUserSig = signal<UserProfile | null | undefined>(undefined);

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
  }

  getUserProfile(): Observable<UserProfile> {
    const userId = this.authService.currentUser?.id;
    
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

  updateUserProfile(userData: UserUpdateDTO): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/update`, userData).pipe(
      tap((userActualizado) => {
        this.currentUserSig.set(userActualizado);
        localStorage.setItem('user', JSON.stringify(userActualizado));
      })
    );
  }
}
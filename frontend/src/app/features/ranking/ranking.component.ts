import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GenericListComponent, TableColumn } from '../../shared/components/list/list.component';
import { UserService } from '../../core/services/user.service';
import { UserProfile } from '../../core/models/user.models';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [CommonModule, GenericListComponent],
  templateUrl: './ranking.component.html'
})
export class RankingComponent implements OnInit {
  private userService = inject(UserService);
  private router = inject(Router);

  usersSignal = signal<UserProfile[]>([]);
  loadingSignal = signal<boolean>(true);
  
  columns: TableColumn[] = [
    { header: 'Nombre', field: 'name' },
    { header: 'Usuario', field: 'email' },
    { header: 'Puntos', field: 'points' },
    { header: 'Perfil', field: 'actions' } 
  ];

  ngOnInit() {
    this.loadRanking();
  }

  loadRanking() {
    this.loadingSignal.set(true);
    this.userService.getAllUsersFiltered({}, 0, 10, 'points', 'DESC').subscribe({
      next: (users) => {
        this.usersSignal.set(users);
        this.loadingSignal.set(false);
      },
      error: (err) => {
        console.error('Error cargando ranking:', err);
        this.usersSignal.set([]);
        this.loadingSignal.set(false);
      }
    });
  }

  verPerfil(user: UserProfile) {
    if (user && user.id) {
        this.router.navigate(['/user', user.id]);
    }
  }
}
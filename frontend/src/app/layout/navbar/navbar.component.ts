import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AlertService } from '../../core/services/alert.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: 'navbar.component.html'
})
export class NavbarComponent {
  profileMenuOpen = false;
  mobileMenuOpen = false;
  isLoggedIn = computed(() => this.authService.isLoggedIn());
  private alertService = inject(AlertService);

  constructor(private router: Router, private authService: AuthService) { }

  toggleProfileMenu() {
    this.profileMenuOpen = !this.profileMenuOpen;
  }

  closeProfileMenu() {
    this.profileMenuOpen = false;
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  logout() {
    this.alertService.confirm(
      '¿Cerrar sesión?',
      '¿Estás seguro que deseas salir?',
      'Sí, salir',
      'Cancelar'
    ).then((result) => {
      if (result.isConfirmed) {
        this.authService.logout();
        this.alertService.success('Sesión cerrada', 'Has cerrado sesión exitosamente');
      }
    });
  }
}
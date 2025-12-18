import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { AlertService } from '../../core/services/alert.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: 'navbar.component.html'
})
export class NavbarComponent {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private alertService = inject(AlertService);
  private router = inject(Router);

  profileMenuOpen = false;
  mobileMenuOpen = false;

  isLoggedIn = computed(() => this.authService.isLoggedIn());
  userData = computed(() => this.userService.currentUser());
  isAdmin = computed(() => this.userData()?.role === 'ADMIN');

  userInitials = computed(() => {
    const user = this.userData();
    if (!user?.name || !user?.lastName) return '?';
    return `${user.name.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  });

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
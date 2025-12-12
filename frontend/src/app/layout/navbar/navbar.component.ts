import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

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
    this.closeProfileMenu();
    this.closeMobileMenu();
    this.authService.logout();
  }
}

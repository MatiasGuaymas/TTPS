import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: 'navbar.component.html'
})
export class NavbarComponent {
  profileMenuOpen = false;
  mobileMenuOpen = false;

  constructor(private router: Router) { }

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
    // Acá va a estar la lógica del logout
    console.log('Cerrando sesión...');
    this.closeProfileMenu();
    this.closeMobileMenu();
    this.router.navigate(['/login']);
  }
}
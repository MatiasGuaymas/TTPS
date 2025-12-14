import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
    const userService = inject(UserService);
    const authService = inject(AuthService);
    const router = inject(Router);

    // Verificar que esté autenticado
    if (!authService.isLoggedIn()) {
        router.navigate(['/login']);
        return false;
    }

    // Verificar que sea admin
    const user = userService.currentUser();
    if (user?.role !== 'ADMIN') {
        router.navigate(['/home']);
        return false;
    }

    return true;
};

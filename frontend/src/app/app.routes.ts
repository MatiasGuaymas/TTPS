import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginComponent } from './features/auth/login/login.component';
import { ProfileComponent } from './features/profile/profile.component';
import { HomeComponent } from './features/home/home.component';
import { AltaMascota } from './features/mascota/pages/alta/alta.component';
import { DetalleComponent } from './features/mascota/pages/detalle/detalle.component';
import { AdminUsersComponent } from './features/admin/admin-users/admin-users.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'home', component: HomeComponent },
    { path: 'mascota/:id', component: DetalleComponent },
    // Rutas para usuarios NO autenticados
    {
        path: 'register',
        component: RegisterComponent,
        canActivate: [guestGuard]
    },
    {
        path: 'login',
        component: LoginComponent,
        canActivate: [guestGuard]
    },
    // Rutas para usuarios autenticados
    {
        path: 'profile',
        component: ProfileComponent,
        canActivate: [authGuard]
    },
    {
        path: 'mascota-perdida',
        component: AltaMascota,
        canActivate: [authGuard]
    },
    // Rutas para administradores
    {
        path: 'admin/users',
        component: AdminUsersComponent,
        canActivate: [adminGuard]
    },
];
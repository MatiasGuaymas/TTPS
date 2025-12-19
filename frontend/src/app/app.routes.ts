import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginComponent } from './features/auth/login/login.component';
import { ProfileComponent } from './features/profile/profile.component';
import { HomeComponent } from './features/home/home.component';
import { AltaMascota } from './features/mascota/pages/alta/alta.component';
import { DetalleComponent } from './features/mascota/pages/detalle/detalle.component';
import { AdminUsersComponent } from './features/admin/admin-users/admin-users.component';
import { NotFoundComponent } from './features/error/not-found/not-found.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { adminGuard } from './core/guards/admin.guard';

import { PetListComponent } from './features/mascota/pages/listado/listado.component';
import { UserPublicProfileComponent } from './features/profile/user-public-profile/user-public-profile.component';
import { PetEditComponent } from './features/mascota/pages/editar/editar.component';
import { PetDeleteComponent } from './features/mascota/pages/eliminar/eliminar.component';
import { RankingComponent } from './features/ranking/ranking.component';

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
    {
        path: 'listado-mascotas',
        component: PetListComponent,

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
    {
        path: 'user/:id',
        component: UserPublicProfileComponent,
        canActivate: [authGuard]
    },
    {
        path:'edicion-mascota/:id',
        component:PetEditComponent,
        canActivate: [authGuard]
    },
    {
        path:'eliminar-mascota/:id',
        component:PetDeleteComponent,
        canActivate: [authGuard]
    },
    // Rutas para administradores
    {
        path: 'admin/users',
        component: AdminUsersComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'ranking',
        component: RankingComponent
    },
    // Ruta 404
    {
        path: '**',
        component: NotFoundComponent
    }

];

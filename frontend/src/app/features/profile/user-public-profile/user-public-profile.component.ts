import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
    selector: 'app-user-public-profile',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './user-public-profile.component.html',
    styleUrls: ['./user-public-profile.component.css']
})
export class UserPublicProfileComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private userService = inject(UserService);

    userProfile$!: Observable<UserPublicProfile>;
    error: string | null = null;

    ngOnInit() {
        const userId = Number(this.route.snapshot.paramMap.get('id'));

        if (!userId) {
            this.error = 'ID de usuario no válido';
            return;
        }

        this.userProfile$ = this.userService.getUserPublicProfile(userId).pipe(
            catchError(err => {
                console.error('Error al cargar perfil:', err);
                this.error = 'No se pudo cargar el perfil del usuario';
                return throwError(() => err);
            })
        );
    }

    goToPetDetail(petId: number) {
        this.router.navigate(['/mascota', petId]);
    }

    goBack() {
        this.router.navigate(['/home']);
    }
}
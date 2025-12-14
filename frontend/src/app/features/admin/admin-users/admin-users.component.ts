import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { AlertService } from '../../../core/services/alert.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-admin-users',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './admin-users.component.html',
    styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
    private userService = inject(UserService);
    private alertService = inject(AlertService);
    private router = inject(Router);

    users: UserProfile[] = [];
    loading = true;

    ngOnInit(): void {
        // this.loadUsers();
    }
    /*
    loadUsers(): void {
        this.loading = true;
        this.userService.getAllUsers().subscribe({
            next: (users) => {
                this.users = users;
                this.loading = false;
            },
            error: (error) => {
                console.error('Error al cargar usuarios:', error);
                this.alertService.error('Error', 'No se pudieron cargar los usuarios');
                this.loading = false;
            }
        });
    }

    toggleUserStatus(user: UserProfile): void {
        const newStatus = !user.enabled;
        const action = newStatus ? 'habilitar' : 'dar de baja';
        const confirmText = newStatus
            ? 'El usuario podrá acceder nuevamente al sistema'
            : 'El usuario no podrá acceder al sistema (baja lógica)';

        this.alertService.confirm(
            `¿Estás seguro de ${action} a ${user.name} ${user.lastName}?`,
            confirmText
        ).then((result) => {
            if (result.isConfirmed) {
                this.userService.updateUserStatus(user.id, newStatus).subscribe({
                    next: (updatedUser) => {
                        const index = this.users.findIndex(u => u.id === user.id);
                        if (index !== -1) {
                            this.users[index] = updatedUser;
                        }
                        const successMsg = newStatus ? 'Usuario habilitado exitosamente' : 'Usuario dado de baja exitosamente';
                        this.alertService.success('Éxito', successMsg);
                    },
                    error: (error) => {
                        console.error('Error al actualizar estado:', error);
                        this.alertService.error('Error', `No se pudo ${action} el usuario`);
                    }
                });
            }
        });
    } */

    getRoleBadgeClass(role: string): string {
        return role === 'ADMIN' ? 'bg-danger' : 'bg-primary';
    }

    getStatusBadgeClass(enabled: boolean): string {
        return enabled ? 'bg-success' : 'bg-secondary';
    }

    getActiveUsersCount(): number {
        return this.users.filter(u => u.enabled).length;
    }

    getInactiveUsersCount(): number {
        return this.users.filter(u => !u.enabled).length;
    }
}

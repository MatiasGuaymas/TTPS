import { Component, OnInit, inject, signal } from '@angular/core';
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

    users = signal<UserProfile[]>([]);
    loading = signal(true);

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.loading.set(true);
        this.userService.getAllUsers().subscribe({
            next: users => {
                this.users.set(users);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    getRoleBadgeClass(role: string): string {
        return role === 'ADMIN' ? 'bg-danger' : 'bg-primary';
    }

    getStatusBadgeClass(enabled: boolean): string {
        return enabled ? 'bg-success' : 'bg-secondary';
    }

    getActiveUsersCount() : number {
        return this.users().filter(user => user.enabled).length;
    }

    getInactiveUsersCount(): number {
        return this.users().filter(user => !user.enabled).length;
    }
}

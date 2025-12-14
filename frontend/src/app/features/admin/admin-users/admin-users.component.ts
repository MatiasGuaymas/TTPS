import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-admin-users',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './admin-users.component.html',
    styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
    private userService = inject(UserService);
    private alertService = inject(AlertService);
    private router = inject(Router);
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);

    users = signal<UserProfile[]>([]);
    loading = signal(true);
    showEditModal = signal(false);
    editingUser = signal<UserProfile | null>(null);
    editForm!: FormGroup;
    
    showCreateModal = signal(false);
    createForm!: FormGroup;

    ngOnInit(): void {
        this.loadUsers();
        this.initializeForm();
        this.initializeCreateForm();
    }

    initializeForm(): void {
        this.editForm = this.fb.group({
            name: ['', [Validators.required, Validators.minLength(2)]],
            lastName: ['', [Validators.required, Validators.minLength(2)]],
            phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9+ -]{6,20}$/)]],
            city: ['', Validators.required],
            neighborhood: ['', Validators.required],
            latitude: [null, [Validators.required, Validators.min(-90), Validators.max(90)]],
            longitude: [null, [Validators.required, Validators.min(-180), Validators.max(180)]],
            role: ['USER', Validators.required]
        });
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

    toggleUserStatus(user: UserProfile): void {
        const currentUserId = this.authService.currentUser()?.id;
        if (user.id === currentUserId) {
            this.alertService.error('Error', 'No puedes cambiar tu propio estado');
            return;
        }

        const newStatus = !user.enabled;
        const action = newStatus ? 'habilitar' : 'deshabilitar';
        const confirmText = newStatus
            ? 'El usuario podrá acceder nuevamente al sistema'
            : 'El usuario no podrá acceder al sistema';

        this.alertService.confirm(
            `¿Estás seguro de ${action} a ${user.name} ${user.lastName}?`,
            confirmText
        ).then((result) => {
            if (result.isConfirmed) {
                this.userService.updateUserStatus(user.id, newStatus).subscribe({
                    next: (updatedUser) => {
                        const currentUsers = this.users();
                        const index = currentUsers.findIndex(u => u.id === user.id);
                        if (index !== -1) {
                            currentUsers[index] = updatedUser;
                            this.users.set([...currentUsers]);
                        }
                        const successMsg = newStatus ? 'Usuario habilitado exitosamente' : 'Usuario deshabilitado exitosamente';
                        this.alertService.success('Éxito', successMsg);
                    },
                    error: (error) => {
                        console.error('Error al actualizar estado:', error);
                        this.alertService.error('Error', `No se pudo ${action} el usuario`);
                    }
                });
            }
        });
    }

    openEditModal(user: UserProfile): void {
        this.editingUser.set(user);
        this.editForm.patchValue({
            name: user.name,
            lastName: user.lastName,
            phoneNumber: user.phone,
            city: user.city,
            neighborhood: user.neighborhood,
            latitude: user.latitude,
            longitude: user.longitude,
            role: user.role
        });
        this.showEditModal.set(true);
    }

    closeEditModal(): void {
        this.showEditModal.set(false);
        this.editingUser.set(null);
        this.editForm.reset();
    }

    saveUserEdit(): void {
        if (this.editForm.invalid) {
            this.alertService.error('Error', 'Por favor complete todos los campos correctamente');
            return;
        }

        const user = this.editingUser();
        if (!user) return;

        const updateData: AdminUserUpdateRequest = this.editForm.value;

        this.userService.adminUpdateUser(user.id, updateData).subscribe({
            next: (updatedUser) => {
                const currentUsers = this.users();
                const index = currentUsers.findIndex(u => u.id === user.id);
                if (index !== -1) {
                    currentUsers[index] = updatedUser;
                    this.users.set([...currentUsers]);
                }
                this.alertService.success('Éxito', 'Usuario actualizado exitosamente');
                this.closeEditModal();
            },
            error: (error) => {
                console.error('Error al actualizar usuario:', error);
                this.alertService.error('Error', 'No se pudo actualizar el usuario');
            }
        });
    }

    initializeCreateForm(): void {
        this.createForm = this.fb.group({
            name: ['', [Validators.required, Validators.minLength(2)]],
            lastName: ['', [Validators.required, Validators.minLength(2)]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9+ -]{6,20}$/)]],
            latitude: [null, [Validators.required, Validators.min(-90), Validators.max(90)]],
            longitude: [null, [Validators.required, Validators.min(-180), Validators.max(180)]]
        });
    }

    openCreateModal(): void {
        this.createForm.reset();
        this.showCreateModal.set(true);
    }

    closeCreateModal(): void {
        this.showCreateModal.set(false);
        this.createForm.reset();
    }

    saveNewAdmin(): void {
        if (this.createForm.invalid) {
            this.alertService.error('Error', 'Por favor complete todos los campos correctamente');
            return;
        }

        const adminData = this.createForm.value;

        this.userService.createAdmin(adminData).subscribe({
            next: (newAdmin) => {
                const currentUsers = this.users();
                this.users.set([...currentUsers, newAdmin]);
                this.alertService.success('Éxito', 'Administrador creado exitosamente');
                this.closeCreateModal();
            },
            error: (error) => {
                console.error('Error al crear administrador:', error);
                const message = error.error?.message || 'No se pudo crear el administrador';
                this.alertService.error('Error', message);
            }
        });
    }
}

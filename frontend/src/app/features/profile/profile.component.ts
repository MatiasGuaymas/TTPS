import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService} from '../../core/services/user.service';
import { AlertService } from '../../core/services/alert.service';
import { UserUpdateDTO } from '../../core/models/user.models';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: 'profile.component.html'
})
export class ProfileComponent implements OnInit {
    private fb = inject(FormBuilder);
    private router = inject(Router);
    private userService = inject(UserService);
    private alerts = inject(AlertService);

    profileForm: FormGroup;
    isEditing = signal(false);
    cargando = signal(true);
    userData = computed(() => { return this.userService.currentUser(); })

    constructor() {
        this.profileForm = this.fb.group({
            name: [{ value: '', disabled: true }, [Validators.required, Validators.minLength(2)]],
            lastName: [{ value: '', disabled: true }, [Validators.required, Validators.minLength(2)]],
            email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
            phoneNumber: [{ value: '', disabled: true }, [Validators.required, Validators.pattern(/^[0-9+ -]{6,20}$/)]],
            city: [{ value: '', disabled: true }, []],
            neighborhood: [{ value: '', disabled: true }, []],
            latitude: [{ value: 0, disabled: true }, [Validators.required, Validators.min(-90), Validators.max(90)]],
            longitude: [{ value: 0, disabled: true }, [Validators.required, Validators.min(-180), Validators.max(180)]],
        });
    }

    ngOnInit() {
        this.cargarDatosUsuario();
    }

    cargarDatosUsuario() {
        this.cargando.set(true);
        this.userService.getUserProfile().subscribe({
            next: (user) => {
                this.profileForm.patchValue({
                    name: user.name,
                    lastName: user.lastName,
                    email: user.email,
                    phoneNumber: user.phone,
                    city: user.city || '',
                    neighborhood: user.neighborhood || '',
                    latitude: user.latitude,
                    longitude: user.longitude
                });
                this.cargando.set(false);
            },
            error: (error) => {
                console.error('Error al cargar datos del usuario:', error);
                this.alerts.error('Error', 'No se pudieron cargar los datos del perfil');
                this.cargando.set(false);
            }
        });
    }

    toggleEdit() {
        this.isEditing.update(value => !value);

        if (this.isEditing()) {
            // Habilitar campos editables
            this.profileForm.get('name')?.enable();
            this.profileForm.get('lastName')?.enable();
            this.profileForm.get('phoneNumber')?.enable();
            this.profileForm.get('city')?.enable();
            this.profileForm.get('neighborhood')?.enable();
            this.profileForm.get('latitude')?.enable();
            this.profileForm.get('longitude')?.enable();
        } else {
            // Deshabilitar y resetear
            this.profileForm.get('name')?.disable();
            this.profileForm.get('lastName')?.disable();
            this.profileForm.get('phoneNumber')?.disable();
            this.profileForm.get('city')?.disable();
            this.profileForm.get('neighborhood')?.disable();
            this.profileForm.get('latitude')?.disable();
            this.profileForm.get('longitude')?.disable();

            // Recargar datos originales
            this.cargarDatosUsuario();
        }
    }

    onSave() {
        if (this.profileForm.valid) {
            const datosActualizados: UserUpdateDTO = {
                name: this.profileForm.get('name')?.value,
                lastName: this.profileForm.get('lastName')?.value,
                phoneNumber: this.profileForm.get('phoneNumber')?.value,
                city: this.profileForm.get('city')?.value,
                neighborhood: this.profileForm.get('neighborhood')?.value,
                latitude: this.profileForm.get('latitude')?.value,
                longitude: this.profileForm.get('longitude')?.value
            };

            this.actualizarPerfil(datosActualizados);
        } else {
            this.alerts.info('Formulario incompleto', 'Por favor completa todos los campos requeridos correctamente');
            Object.keys(this.profileForm.controls).forEach(key => {
                const control = this.profileForm.get(key);
                if (control?.invalid) {
                    control.markAsTouched();
                }
            });
        }
    }

    private actualizarPerfil(datos: UserUpdateDTO) {
        this.userService.updateUserProfile(datos).subscribe({
            next: () => {
                this.alerts.success('¡Perfil actualizado!', 'Tus datos se guardaron correctamente').then(() => {
                    this.toggleEdit();
                });
            },
            error: (error) => {
                console.error('Error al actualizar perfil:', error);
                const message = error.error?.message || 'No se pudo actualizar el perfil';
                this.alerts.error('Error', message);
            }
        });
    }

    getErrorMessage(fieldName: string): string {
        const control = this.profileForm.get(fieldName);
        if (control?.hasError('required')) {
            return 'Este campo es requerido';
        }
        if (control?.hasError('minlength')) {
            return `Mínimo ${control.errors?.['minlength'].requiredLength} caracteres`;
        }
        if (control?.hasError('email')) {
            return 'Email inválido';
        }
        if (control?.hasError('pattern')) {
            return 'Formato inválido (solo números, espacios, guiones y +)';
        }
        if (control?.hasError('min')) {
            return `Valor mínimo: ${control.errors?.['min'].min}`;
        }
        if (control?.hasError('max')) {
            return `Valor máximo: ${control.errors?.['max'].max}`;
        }
        return '';
    }

    getLocation() {
        if (navigator.geolocation) {
            this.alerts.info('Obteniendo ubicación...', 'Por favor permite el acceso a tu ubicación');

            navigator.geolocation.getCurrentPosition(
                (position) => {
                    this.profileForm.patchValue({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                    this.alerts.success('¡Ubicación obtenida!', 'Tu ubicación se actualizó correctamente');
                },
                (error) => {
                    console.error('Error al obtener ubicación:', error);
                    this.alerts.error('Error de ubicación', 'No se pudo obtener tu ubicación. Verifica los permisos.');
                }
            );
        } else {
            this.alerts.error('Navegador no compatible', 'Tu navegador no soporta geolocalización.');
        }
    }

}

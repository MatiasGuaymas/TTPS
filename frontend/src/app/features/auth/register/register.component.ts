import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterFormContent } from '../../../core/models/auth.models';
import { AlertService } from '../../../core/services/alert.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: 'register.component.html'
})
export class RegisterComponent {
    private alerts = inject(AlertService);
    registerForm: FormGroup<RegisterFormContent>;
    ubicacionLista = signal(false);
    cargandoUbicacion = signal(false);

    constructor(
        private fb: NonNullableFormBuilder,
        private router: Router,
        private authService: AuthService
    ) {
        this.registerForm = this.fb.group<RegisterFormContent>({
            name: this.fb.control('', [Validators.required, Validators.minLength(2)]),
            lastName: this.fb.control('', [Validators.required, Validators.minLength(2)]),
            email: this.fb.control('', [Validators.required, Validators.email]),
            phoneNumber: this.fb.control('', [Validators.required, Validators.pattern(/^\+?\d{7,15}$/)]),
            password: this.fb.control('', [Validators.required, Validators.minLength(6)]),
            latitude: this.fb.control(0, [Validators.required]),
            longitude: this.fb.control(0, [Validators.required])
        });
    }

    onSubmit() {
        if (this.registerForm.valid) {
            console.log('Formulario válido:', this.registerForm.value);
            this.authService.register(this.registerForm.getRawValue()).subscribe({
                next: (response) => {
                    this.alerts.success('¡Cuenta creada!', 'Te has registrado exitosamente').then(() => {
                        this.router.navigate(['/login']);
                    });
                },
                error: (error) => {
                    console.error('Error en el registro:', error);
                    this.alerts.error('No se pudo completar el registro. Intenta nuevamente.');
                }
            });
        } else {
            this.alerts.info('Formulario incompleto', 'Por favor completa todos los campos correctamente');
            Object.keys(this.registerForm.controls).forEach(key => {
                const control = this.registerForm.get(key);
                if (control?.invalid) {
                    control.markAsTouched();
                }
            });
        }
    }

    getLocation() {
        if (navigator.geolocation) {
            this.cargandoUbicacion.set(true);
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    // Éxito
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;

                    // Actualizo el formulario
                    this.registerForm.patchValue({
                        latitude: lat,
                        longitude: lng
                    });

                    this.ubicacionLista.set(true);
                    this.cargandoUbicacion.set(false);
                    this.alerts.success('¡Ubicación obtenida!', 'Tu ubicación se cargó correctamente');
                },
                (error) => {
                    // Error
                    console.error(error);
                    this.cargandoUbicacion.set(false);
                    this.alerts.error("No pudimos obtener tu ubicación. Por favor permite el acceso.");
                }
            );
        } else {
            this.alerts.error("Tu navegador no soporta geolocalización.");
        }
    }

    getErrorMessage(fieldName: string): string {
        const control = this.registerForm.get(fieldName);
        if (control?.hasError('required')) {
            return 'Este campo es requerido';
        }
        if (control?.hasError('email')) {
            return 'Email inválido';
        }
        if (control?.hasError('minlength')) {
            return `Mínimo ${control.errors?.['minlength'].requiredLength} caracteres`;
        }
        if (control?.hasError('pattern')) {
            return 'Formato inválido';
        }
        return '';
    }
}

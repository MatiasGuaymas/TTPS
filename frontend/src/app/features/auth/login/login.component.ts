import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AlertService } from '../../../core/services/alert.service';
import { LoginFormContent } from '../../../core/models/auth.models';
import { AuthService } from '../../../core/services/auth.service';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: 'login.component.html'
})
export class LoginComponent {
    private route = inject(ActivatedRoute);
    private alerts = inject(AlertService);
    loginForm: FormGroup<LoginFormContent>;

    constructor(
        private fb: NonNullableFormBuilder,
        private router: Router,
        private authService: AuthService
    ) {
        this.loginForm = this.fb.group({
            email: this.fb.control<string>('', [Validators.required, Validators.email]),
            password: this.fb.control<string>('', [Validators.required, Validators.minLength(6)])
        });
    }

    onSubmit() {
        if (this.loginForm.valid) {
            console.log('Formulario válido:', this.loginForm.value);
            this.authService.login(this.loginForm.getRawValue()).subscribe({
                next: (response) => {
                    this.alerts.success('¡Bienvenido!', 'Inicio de sesión exitoso').then(() => {
                        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home';
                        this.router.navigateByUrl(returnUrl);
                    });
                },
                error: (error) => {
                    console.error('Error en el login:', error);
                    this.alerts.error('Error de autenticación', 'Email o contraseña incorrectos');
                }
            });
        } else {
            this.alerts.info('Formulario incompleto', 'Por favor completa todos los campos correctamente');
            Object.keys(this.loginForm.controls).forEach(key => {
                const control = this.loginForm.get(key);
                if (control?.invalid) {
                    control.markAsTouched();
                }
            });
        }
    }

    getErrorMessage(fieldName: string): string {
        const control = this.loginForm.get(fieldName);
        if (control?.hasError('required')) {
            return 'Este campo es requerido';
        }
        if (control?.hasError('email')) {
            return 'Email inválido';
        }
        if (control?.hasError('minlength')) {
            return `Mínimo ${control.errors?.['minlength'].requiredLength} caracteres`;
        }
        return '';
    }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: 'profile.component.html'
})
export class ProfileComponent {
    profileForm: FormGroup;
    isEditing = false;

    // Datos del usuario (esto vendría de un servicio)
    userData = {
        nombre: 'Juan Pablo',
        apellido: 'Perez',
        email: 'juanpablo@gmail.com',
        telefono: '+54 9 221234567',
        barrio: 'City Bell',
        ciudad: 'La Plata'
    };

    constructor(
        private fb: FormBuilder,
        private router: Router
    ) {
        this.profileForm = this.fb.group({
            nombre: [{ value: this.userData.nombre, disabled: true }, [Validators.required, Validators.minLength(2)]],
            apellido: [{ value: this.userData.apellido, disabled: true }, [Validators.required, Validators.minLength(2)]],
            barrio: [{ value: this.userData.barrio, disabled: true }, [Validators.required]],
            ciudad: [{ value: this.userData.ciudad, disabled: true }, [Validators.required]],
            contrasenaActual: [{ value: '', disabled: true }, []],
            contrasenaNueva: [{ value: '', disabled: true }, []]
        });
    }

    toggleEdit() {
        this.isEditing = !this.isEditing;

        if (this.isEditing) {
            // Habilitar campos editables
            this.profileForm.get('nombre')?.enable();
            this.profileForm.get('apellido')?.enable();
            this.profileForm.get('barrio')?.enable();
            this.profileForm.get('ciudad')?.enable();
            this.profileForm.get('contrasenaActual')?.enable();
            this.profileForm.get('contrasenaNueva')?.enable();
        } else {
            // Deshabilitar y resetear
            this.profileForm.patchValue({
                nombre: this.userData.nombre,
                apellido: this.userData.apellido,
                barrio: this.userData.barrio,
                ciudad: this.userData.ciudad,
                contrasenaActual: '',
                contrasenaNueva: ''
            });

            this.profileForm.get('nombre')?.disable();
            this.profileForm.get('apellido')?.disable();
            this.profileForm.get('barrio')?.disable();
            this.profileForm.get('ciudad')?.disable();
            this.profileForm.get('contrasenaActual')?.disable();
            this.profileForm.get('contrasenaNueva')?.disable();
        }
    }

    onSave() {
        if (this.profileForm.valid) {
            const formValue = this.profileForm.getRawValue();

            // Validar cambio de contraseña
            if (formValue.contrasenaActual || formValue.contrasenaNueva) {
                if (!formValue.contrasenaActual || !formValue.contrasenaNueva) {
                    alert('Debes completar ambos campos de contraseña');
                    return;
                }
                if (formValue.contrasenaNueva.length < 6) {
                    alert('La nueva contraseña debe tener al menos 6 caracteres');
                    return;
                }
            }

            console.log('Datos a guardar:', formValue);

            // Actualizar datos locales (esto debería ser una llamada al backend)
            this.userData = {
                ...this.userData,
                nombre: formValue.nombre,
                apellido: formValue.apellido,
                barrio: formValue.barrio,
                ciudad: formValue.ciudad
            };

            // Salir del modo edición
            this.isEditing = false;
            this.toggleEdit();

            alert('Perfil actualizado correctamente');
        }
    }

    getErrorMessage(fieldName: string): string {
        const control = this.profileForm.get(fieldName);
        if (control?.hasError('required')) {
            return 'Este campo es requerido';
        }
        if (control?.hasError('minlength')) {
            return `Mínimo ${control.errors?.['minlength'].requiredLength} caracteres`;
        }
        return '';
    }
    
}
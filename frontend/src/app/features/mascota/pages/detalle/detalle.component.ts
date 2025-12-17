import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MascotaService } from '../../mascota.service';
import { Pet, EstadoMascota, TamanoMascota, TipoMascota } from '../../mascota.model';
import { AlertService } from '../../../../core/services/alert.service';

@Component({
    selector: 'app-pet-detalle',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './detalle.component.html'
})
export class DetalleComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private mascotaService = inject(MascotaService);
    private alertService = inject(AlertService);

    pet = signal<Pet | null>(null);
    loading = signal(true);
    currentPhotoIndex = signal(0);

    ngOnInit(): void {
        const petId = this.route.snapshot.paramMap.get('id');
        if (petId) {
            this.loadPetDetails(+petId);
        } else {
            this.alertService.error('Error', 'ID de mascota no válido');
            this.router.navigate(['/home']);
        }
    }

    loadPetDetails(id: number): void {
        this.loading.set(true);
        this.mascotaService.getPetById(id).subscribe({
            next: (pet) => {
                this.pet.set(pet);
                this.loading.set(false);
            },
            error: (error) => {
                console.error('Error al cargar mascota:', error);
                this.alertService.error('Error', 'No se pudo cargar la información de la mascota');
                this.loading.set(false);
                this.router.navigate(['/home']);
            }
        });
    }

    nextPhoto(): void {
        const pet = this.pet();
        if (pet && pet.photosBase64.length > 1) {
            this.currentPhotoIndex.set((this.currentPhotoIndex() + 1) % pet.photosBase64.length);
        }
    }

    prevPhoto(): void {
        const pet = this.pet();
        if (pet && pet.photosBase64.length > 1) {
            const newIndex = this.currentPhotoIndex() - 1;
            this.currentPhotoIndex.set(newIndex < 0 ? pet.photosBase64.length - 1 : newIndex);
        }
    }

    getEstadoLabel(estado: EstadoMascota): string {
        const labels = {
            [EstadoMascota.PERDIDO_PROPIO]: 'Perdido (Propio)',
            [EstadoMascota.PERDIDO_AJENO]: 'Perdido (Ajeno)',
            [EstadoMascota.RECUPERADO]: 'Recuperado',
            [EstadoMascota.ADOPTADO]: 'Adoptado'
        };
        return labels[estado] || estado;
    }

    getTamanoLabel(tamano: TamanoMascota): string {
        const labels = {
            [TamanoMascota.PEQUENO]: 'Pequeño',
            [TamanoMascota.MEDIANO]: 'Mediano',
            [TamanoMascota.GRANDE]: 'Grande'
        };
        return labels[tamano] || tamano;
    }

    getTipoLabel(tipo: TipoMascota): string {
        const labels = {
            [TipoMascota.PERRO]: 'Perro',
            [TipoMascota.GATO]: 'Gato',
            [TipoMascota.COBAYA]: 'Cobaya',
            [TipoMascota.LORO]: 'Loro',
            [TipoMascota.CONEJO]: 'Conejo',
            [TipoMascota.CABALLO]: 'Caballo',
            [TipoMascota.TORTUGA]: 'Tortuga'
        };
        return labels[tipo] || tipo;
    }

    getEstadoBadgeClass(estado: EstadoMascota): string {
        const classes = {
            [EstadoMascota.PERDIDO_PROPIO]: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
            [EstadoMascota.PERDIDO_AJENO]: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
            [EstadoMascota.RECUPERADO]: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
            [EstadoMascota.ADOPTADO]: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
        };
        return classes[estado] || 'bg-gray-100 text-gray-800';
    }
}

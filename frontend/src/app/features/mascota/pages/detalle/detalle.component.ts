import { Component, OnInit, OnDestroy, AfterViewInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MascotaService } from '../../mascota.service';
import { AvistamientoService } from '../../avistamiento.service';
import { PetResponse, State, Size, TipoMascota } from '../../mascota.model';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Map } from '../../../../shared/components/map/map';
import * as L from 'leaflet';
import { initFlowbite } from 'flowbite';

// TODO: Agregar foto a avistamiento -> no funciono :(

@Component({
    selector: 'app-pet-detalle',
    standalone: true,
    imports: [CommonModule, RouterLink, ReactiveFormsModule, Map],
    templateUrl: './detalle.component.html',
    styles: [`
        #map, #sightingMap {
            height: 400px;
            border-radius: 0.5rem;
        }
        #sightingMap {
            height: 16rem;
        }
    `]
})
export class DetalleComponent implements OnInit, AfterViewInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private fb = inject(FormBuilder);
    private mascotaService = inject(MascotaService);
    private avistamientoService = inject(AvistamientoService);
    private alertService = inject(AlertService);
    private authService = inject(AuthService);

    pet = signal<PetResponse | null>(null);
    loading = signal(true);
    currentPhotoIndex = signal(0);
    showSightingModal = signal(false);
    submittingSighting = signal(false);
    photoPreview = signal<string | ArrayBuffer | null>(null);
    photoBase64 = signal<string | undefined>(undefined);
    maxDate = new Date().toISOString().split('T')[0];
    sightings = signal<SightingResponse[]>([]);
    loadingSightings = signal(false);

    sightingForm!: FormGroup;
    selectedSightingLocation = signal<{ latitude: number, longitude: number } | null>(null);

    ngOnInit(): void {
        // Inicializar formulario de avistamiento
        this.sightingForm = this.fb.group({
            date: [new Date().toISOString().split('T')[0], [Validators.required]],
            comment: ['', [Validators.maxLength(200)]],
            photoFile: [null, Validators.required]
        });

        const iconRetinaUrl = 'assets/leaflet/marker-icon-2x.png';
        const iconUrl = 'assets/leaflet/marker-icon.png';
        const shadowUrl = 'assets/leaflet/marker-shadow.png';
        const iconDefault = L.icon({
            iconRetinaUrl,
            iconUrl,
            shadowUrl,
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            tooltipAnchor: [16, -28],
            shadowSize: [41, 41]
        });
        L.Marker.prototype.options.icon = iconDefault;

        const petId = this.route.snapshot.paramMap.get('id');
        if (petId) {
            this.loadPetDetails(+petId);
        } else {
            this.alertService.error('Error', 'ID de mascota no válido');
            this.router.navigate(['/home']);
        }
    }

    ngAfterViewInit(): void {
        initFlowbite();
    }

    loadPetDetails(id: number): void {
        this.loading.set(true);
        this.mascotaService.getPetById(id).subscribe({
            next: (pet) => {
                this.pet.set(pet);
                this.loading.set(false);
                // Cargar avistamientos de esta mascota
                this.loadSightings(id);
            },
            error: (error) => {
                console.error('Error al cargar mascota:', error);
                this.alertService.error('Error', 'No se pudo cargar la información de la mascota');
                this.loading.set(false);
                this.router.navigate(['/home']);
            }
        });
    }

    loadSightings(petId: number): void {
        this.loadingSightings.set(true);
        this.avistamientoService.getSightingsByPetId(petId).subscribe({
            next: (sightings) => {
                this.sightings.set(sightings);
                this.loadingSightings.set(false);
            },
            error: (error) => {
                if (error.status === 204) {
                    this.sightings.set([]);
                } else {
                    console.error('Error al cargar avistamientos:', error);
                }
                this.loadingSightings.set(false);
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

    getEstadoLabel(estado: State): string {
        const labels = {
            [State.PERDIDO_PROPIO]: 'Perdido (Propio)',
            [State.PERDIDO_AJENO]: 'Perdido (Ajeno)',
            [State.RECUPERADO]: 'Recuperado',
            [State.ADOPTADO]: 'Adoptado'
        };
        return labels[estado] || estado;
    }

    getTamanoLabel(tamano: Size): string {
        const labels = {
            [Size.PEQUENO]: 'Pequeño',
            [Size.MEDIANO]: 'Mediano',
            [Size.GRANDE]: 'Grande'
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

    getEstadoBadgeClass(estado: State): string {
        const classes = {
            [State.PERDIDO_PROPIO]: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
            [State.PERDIDO_AJENO]: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
            [State.RECUPERADO]: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
            [State.ADOPTADO]: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
        };
        return classes[estado] || 'bg-gray-100 text-gray-800';
    }

    openSightingModal(): void {
        // Verificar si el usuario está autenticado
        if (!this.authService.isLoggedIn()) {
            this.alertService.error('Autenticación requerida', 'Debes iniciar sesión para reportar un avistamiento');
            this.router.navigate(['/login']);
            return;
        }

        this.showSightingModal.set(true);
        this.sightingForm.reset({
            date: new Date().toISOString().split('T')[0],
            comment: '',
            photoFile: null
        });
        this.selectedSightingLocation.set(null);

        // Re-inicializar flowbite después de abrir el modal
        setTimeout(() => initFlowbite(), 100);
    }

    closeSightingModal(): void {
        this.showSightingModal.set(false);
        this.selectedSightingLocation.set(null);
        // Re-inicializar flowbite después de cerrar el modal
        setTimeout(() => initFlowbite(), 100);
    }

    onSightingLocationChanged(coords: { latitude: number, longitude: number }): void {
        this.selectedSightingLocation.set(coords);
    }

    onPhotoSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files[0]) {
            const file = input.files[0];

            // Validar tipo de archivo
            if (!file.type.startsWith('image/')) {
                this.alertService.error('Error', 'Solo se permiten archivos de imagen');
                return;
            }

            // Validar tamaño (máx 5MB)
            if (file.size > 5 * 1024 * 1024) {
                this.alertService.error('Error', 'La imagen no puede superar los 5MB');
                return;
            }

            const reader = new FileReader();
            reader.onload = e => {
                this.photoPreview.set(reader.result);
            };
            reader.readAsDataURL(file);

            this.convertToBase64(file);
        }
    }

    private convertToBase64(file: File): void {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
            const base64String = (reader.result as string).split(',')[1];
            this.photoBase64.set(base64String);
        }
        reader.onerror = (error) => {
            console.error('Error al convertir a Base64: ', error);
            this.photoBase64.set(undefined);
        };
    }

    submitSighting(): void {
        if (this.sightingForm.invalid) {
            this.alertService.error('Error', 'Por favor completa todos los campos requeridos');
            return;
        }

        const location = this.selectedSightingLocation();
        if (!location) {
            this.alertService.error('Error', 'Por favor selecciona una ubicación en el mapa');
            return;
        }

        const pet = this.pet();
        if (!pet) return;

        this.submittingSighting.set(true);

        const sightingData = {
            petId: pet.id,
            latitude: location.latitude,
            longitude: location.longitude,
            date: this.sightingForm.value.date,
            photoBase64: this.photoBase64(),
            comment: this.sightingForm.value.comment || ''
        };

        this.avistamientoService.createSighting(sightingData).subscribe({
            next: () => {
                this.submittingSighting.set(false);
                this.alertService.success('Éxito', 'Avistamiento reportado correctamente');
                this.closeSightingModal();
                // Recargar avistamientos
                this.loadSightings(pet.id);
            },
            error: (error) => {
                this.submittingSighting.set(false);
                console.error('Error al reportar avistamiento:', error);
                this.alertService.error('Error', 'No se pudo reportar el avistamiento');
            }
        });
    }

    decodePhoto(photoBase64: string): string {
        return `data:image/jpeg;base64,${photoBase64}`;
    }
}

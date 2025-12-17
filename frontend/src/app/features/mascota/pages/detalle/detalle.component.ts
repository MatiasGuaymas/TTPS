import { Component, OnInit, OnDestroy, AfterViewInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MascotaService } from '../../mascota.service';
import { AvistamientoService } from '../../avistamiento.service';
import { Pet, EstadoMascota, TamanoMascota, TipoMascota } from '../../mascota.model';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';
import * as L from 'leaflet';
import { initFlowbite } from 'flowbite';

// TODO: Agregar foto a avistamiento -> no funciono :(

@Component({
    selector: 'app-pet-detalle',
    standalone: true,
    imports: [CommonModule, RouterLink, ReactiveFormsModule],
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
export class DetalleComponent implements OnInit, AfterViewInit, OnDestroy {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private fb = inject(FormBuilder);
    private mascotaService = inject(MascotaService);
    private avistamientoService = inject(AvistamientoService);
    private alertService = inject(AlertService);
    private authService = inject(AuthService);

    pet = signal<Pet | null>(null);
    loading = signal(true);
    currentPhotoIndex = signal(0);
    showSightingModal = signal(false);
    submittingSighting = signal(false);
    photoPreview = signal<string | null>(null);
    maxDate = new Date().toISOString().split('T')[0];
    sightings = signal<SightingResponse[]>([]);
    loadingSightings = signal(false);

    sightingForm!: FormGroup;
    private map: L.Map | null = null;
    private sightingMap: L.Map | null = null;

    ngOnInit(): void {
        // Inicializar formulario de avistamiento
        this.sightingForm = this.fb.group({
            date: [new Date().toISOString().split('T')[0], [Validators.required]],
            comment: ['', [Validators.maxLength(200)]]
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
                // Inicializar mapa después de cargar los datos
                setTimeout(() => this.initMap(), 100);
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
                // Inicializar mapas de avistamientos después de que se rendericen
                setTimeout(() => this.initSightingMaps(), 200);
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

    initMap(): void {
        const pet = this.pet();
        if (!pet || this.map) return;

        // Crear el mapa centrado en la ubicación de pérdida
        this.map = L.map('map').setView([pet.latitude, pet.longitude], 15);

        // Agregar capa de tiles de OpenStreetMap
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap contributors'
        }).addTo(this.map);

        // Agregar marcador en la ubicación de pérdida
        const marker = L.marker([pet.latitude, pet.longitude]).addTo(this.map);
        marker.bindPopup(`<b>${pet.name}</b><br>Último lugar visto`).openPopup();
    }

    initSightingMaps(): void {
        const sightingsList = this.sightings();
        if (!sightingsList || sightingsList.length === 0) return;

        sightingsList.forEach(sighting => {
            const mapId = `map-sighting-${sighting.id}`;
            const mapElement = document.getElementById(mapId);

            if (mapElement && !mapElement.classList.contains('leaflet-container')) {
                const map = L.map(mapId, {
                    zoomControl: true,
                    scrollWheelZoom: true,
                    doubleClickZoom: true
                }).setView([sighting.latitude, sighting.longitude], 15);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                // Agregar marcador en la ubicación del avistamiento
                const marker = L.marker([sighting.latitude, sighting.longitude]).addTo(map);
                marker.bindPopup(`Avistamiento: ${new Date(sighting.date).toLocaleDateString()}`);
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
            photo: null
        });
        this.photoPreview.set(null);

        // Inicializar mapa del modal después de que se muestre
        setTimeout(() => this.initSightingMap(), 200);
    }

    closeSightingModal(): void {
        this.showSightingModal.set(false);
        if (this.sightingMap) {
            this.sightingMap.remove();
            this.sightingMap = null;
        }
        // Re-inicializar flowbite después de cerrar el modal
        setTimeout(() => initFlowbite(), 100);
    }

    initSightingMap(): void {
        const pet = this.pet();
        if (!pet || this.sightingMap) return;

        // Crear el mapa para seleccionar ubicación del avistamiento
        this.sightingMap = L.map('sightingMap').setView([pet.latitude, pet.longitude], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap contributors'
        }).addTo(this.sightingMap);

        // Agregar marcador inicial
        const marker = L.marker([pet.latitude, pet.longitude]).addTo(this.sightingMap);
        marker.bindPopup('Haz clic en el mapa para seleccionar la ubicación del avistamiento').openPopup();

        // Variable para almacenar la ubicación seleccionada
        let selectedLocation = { lat: pet.latitude, lng: pet.longitude };

        // Manejar clics en el mapa
        this.sightingMap.on('click', (e: L.LeafletMouseEvent) => {
            selectedLocation = e.latlng;
            marker.setLatLng(e.latlng);
            marker.bindPopup(`Ubicación seleccionada: ${e.latlng.lat.toFixed(6)}, ${e.latlng.lng.toFixed(6)}`).openPopup();

            // Actualizar el formulario con las nuevas coordenadas
            this.sightingForm.patchValue({
                latitude: e.latlng.lat,
                longitude: e.latlng.lng
            });
        });
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
            reader.onload = (e) => {
                const base64 = e.target?.result as string;
                this.photoPreview.set(base64);
                this.sightingForm.patchValue({ photo: base64 });
            };
            reader.readAsDataURL(file);
        }
    }

    submitSighting(): void {
        if (this.sightingForm.invalid) {
            this.alertService.error('Error', 'Por favor completa todos los campos requeridos');
            return;
        }

        const pet = this.pet();
        if (!pet || !this.sightingMap) return;

        // Obtener la posición del marcador
        const layers = (this.sightingMap as any)._layers;
        let markerPosition = { lat: pet.latitude, lng: pet.longitude };

        for (const key in layers) {
            const layer = layers[key];
            if (layer instanceof L.Marker) {
                markerPosition = layer.getLatLng();
                break;
            }
        }

        this.submittingSighting.set(true);

        const sightingData = {
            petId: pet.id,
            latitude: markerPosition.lat,
            longitude: markerPosition.lng,
            date: this.sightingForm.value.date,
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

    ngOnDestroy(): void {
        if (this.map) {
            this.map.remove();
            this.map = null;
        }
        if (this.sightingMap) {
            this.sightingMap.remove();
            this.sightingMap = null;
        }
    }
}

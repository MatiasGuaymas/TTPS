import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MascotaService } from '../../mascota.service';

import { AlertService } from '../../../../core/services/alert.service';
import { PetFilter, PetResponse, Size, State, TipoMascota } from '../../mascota.model';
import { Router } from '@angular/router';
import { GeolocationService } from '../../../../core/services/geolocation.service';

@Component({
    selector: 'listado-mascotas',
    imports: [ReactiveFormsModule],
    templateUrl: './listado.component.html',
    styleUrl: './listado.component.css',
})
export class PetListComponent implements OnInit{

    pets = signal<PetResponse[]>([]);
    loading = signal(true);

    private geoService = inject(GeolocationService);

    // Paginación
    currentPage = signal(0);
    pageSize = signal(6);
    totalPets = signal(0);
    totalPages = signal(0);

    // Filtros
    filters = signal<PetFilter>({});
    filterForm!: FormGroup;

    states = Object.values(State);
    sizes = Object.values(Size);
    types = Object.values(TipoMascota);

    cargandoUbicacion = signal(false);

    sortOptions = [
        { label: 'Fecha de Pérdida (Más Reciente)', value: {param: "lostDate", direction: "DESC"} },
        { label: 'Fecha de Pérdida (Más Antigua)', value: {param: "lostDate", direction: "ASC"} },
        { label: 'Nombre (A-Z)', value: {param: "name", direction: "ASC"} },
        { label: 'Peso (Mayor a Menor)', value: {param: "weight", direction: "DESC"} },
    ];

    constructor(private fb: FormBuilder, private petService: MascotaService, private alerts: AlertService, private router: Router) {}

    ngOnInit(): void {
        this.initializeFilterForm();
        this.loadPets();
    }

    initializeFilterForm(): void {
        this.filterForm = this.fb.group({
            name: [''],
            state: [null],
            type: [null],
            sort: [{param: "lostDate", direction: "DESC"}],
            petSize: [null],
            weightMin: [null],
            weightMax: [null],
            initialLostDate: [null],
            finalLostDate: [null],
            userLatitude: [null],
            userLongitude: [null],
            maxDistanceInKm: [null],
        });
    }

    loadPets() {
        this.loading.set(true);
        const currentFilters = this.filters();
        this.petService.listAllPets(
            currentFilters,
            this.currentPage(),
            this.pageSize(),
            this.filterForm.value.sort.param,
            this.filterForm.value.sort.direction
        ).subscribe({
            next: pets => {
                this.pets.set(pets || []);
                this.totalPets.set(pets ? pets.length : 0);
                this.loading.set(false);
            },
            error: (error) => {
                console.error('Error al cargar mascotas:', error);
                // Sin contenido
                if (error.status === 204) {
                    this.pets.set([]);
                    this.totalPets.set(0);
                }
                this.loading.set(false);
            }
        });
    }

    loadUserLocation() {
    this.cargandoUbicacion.set(true);
    this.alerts.info('Obteniendo ubicación...', 'Por favor permite el acceso');

    this.geoService.getLocation()
        .then(coords => {
            this.filterForm.patchValue({
                userLatitude: coords.latitude,
                userLongitude: coords.longitude,
                maxDistanceInKm: 10
            });
            this.alerts.success('Ubicación obtenida', 'Ahora podes filtrar por cercanía');
        })
        .catch(err => {
            this.alerts.error('Error', 'No se pudo obtener la ubicación: ' + err);
        })
        .finally(() => this.cargandoUbicacion.set(false));
    }


    applyFilters(): void {
        // Limpiar valores vacíos del formulario
        const formValues = this.filterForm.value;
        const cleanedFilters: PetFilter = {};

        if (formValues.name?.trim()) cleanedFilters.name = formValues.name.trim();
        if (formValues.state?.trim()) cleanedFilters.state = formValues.state.trim();
        if (formValues.type?.trim()) cleanedFilters.type = formValues.type.trim();
        if (formValues.size?.trim()) cleanedFilters.size = formValues.size.trim();
        if (formValues.color?.trim()) cleanedFilters.color = formValues.color.trim();
        if (formValues.race?.trim()) cleanedFilters.race = formValues.race.trim();

        if (formValues.weightMin !== null && formValues.weightMin !== undefined && formValues.weightMin !== '' && formValues.weightMin > 0) {
            cleanedFilters.weightMin = Number(formValues.weightMin);
        }
        if (formValues.weightMax !== null && formValues.weightMax !== undefined && formValues.weightMax !== '' && formValues.weightMax > 0) {
            cleanedFilters.weightMax = Number(formValues.weightMax);
        }

        if (formValues.userLatitude !== null && formValues.userLatitude !== undefined && formValues.userLatitude !== '') {
        cleanedFilters.userLatitude = Number(formValues.userLatitude);
        }
        if (formValues.userLongitude !== null && formValues.userLongitude !== undefined && formValues.userLongitude !== '') {
            cleanedFilters.userLongitude = Number(formValues.userLongitude);
        }
        if (formValues.maxDistanceInKm !== null && formValues.maxDistanceInKm !== undefined && formValues.maxDistanceInKm !== '') {
            cleanedFilters.maxDistanceInKm = Number(formValues.maxDistanceInKm);
        }

        if (formValues.initialLostDate) {
            cleanedFilters.initialLostDate = formValues.initialLostDate;
        }
        if (formValues.finalLostDate) {
            cleanedFilters.finalLostDate = formValues.finalLostDate;
        }

        this.filters.set(cleanedFilters);
        this.currentPage.set(0); // Resetear a la primera página
        this.loadPets();
    }

    decodePhoto(photoBase64: string): string {
        return `data:image/jpeg;base64,${photoBase64}`;
    }

    clearFilters(): void {
        this.filterForm.reset();
        this.filters.set({});
        this.currentPage.set(0);
        this.loadPets();
    }

    changePage(page: number): void {
        if (page >= 0) {
            this.currentPage.set(page);
            this.loadPets();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    }

    nextPage(): void {
        if (this.currentPage() < this.totalPages() - 1) {
            this.changePage(this.currentPage() + 1);
        }
    }

    previousPage(): void {
        if (this.currentPage() > 0) {
            this.changePage(this.currentPage() - 1);
        }
    }

    changePageSize(size: number): void {
        this.pageSize.set(size);
        this.currentPage.set(0);
        this.loadPets();
    }

    goToPetDetail(petId: number): void {
        this.router.navigate(['/mascota', petId]);
    }



}

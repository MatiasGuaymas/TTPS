import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MascotaService } from '../../mascota.service';
import { combineLatest, debounceTime, distinctUntilChanged, Observable, race, startWith, Subject, switchMap } from 'rxjs';
import { GeolocationService } from '../../../../core/services/geolocation.service';
import { AlertService } from '../../../../core/services/alert.service';
import { PetFilter, PetResponse } from '../../mascota.model';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'listado-mascotas',
  imports: [ReactiveFormsModule],
  templateUrl: './listado.html',
  styleUrl: './listado.css',
})


export class ListadoMascotas implements OnInit{

  filterForm:FormGroup;
  private geolocationService = inject(GeolocationService);
  private alerts = inject(AlertService);

  sortOptions = [
    { label: 'Fecha de Pérdida (Más Reciente)', value: 'lostDate,desc' },
    { label: 'Fecha de Pérdida (Más Antigua)', value: 'lostDate,asc' },
    { label: 'Nombre (A-Z)', value: 'name,asc' },
    { label: 'Peso (Mayor a Menor)', value: 'weight,desc' },
  ];

  userCurrentLocation=new FormControl(false);
  cargandoUbicacion=new FormControl(false);

  private searchTrigger$ = new Subject<void>();

  constructor(private fb:FormBuilder, private petService: MascotaService, private alert: AlertService) {
    this.filterForm = new FormGroup({
      name: new FormControl(''),
      state: new FormControl(null),
      type: new FormControl(null),
      size: new FormControl(null),
      color: new FormControl(''),
      race: new FormControl(''),
      weightMin: new FormControl(null),
      weightMax: new FormControl(null),
      initialLostDate: new FormControl(''),
      finalLostDate: new FormControl(''),
      page: new FormControl(0),
      perPage: new FormControl(10),
      sort: new FormControl('lostDate,desc'),

      userLatitude:new FormControl<number|null>(null),
      userLongitude:new FormControl<number|null>(null),
      maxDistanceInKm: new FormControl(10),
    });

    this.userCurrentLocation.valueChanges.subscribe(
      (useLocation) => {
        if(useLocation) {
             this.loadUserLocation();
        } else {
             this.filterForm.get('userLatitude')?.setValue(null);
             this.filterForm.get('userLongitude')?.setValue(null);
        }
      }
    )
  }

  async loadUserLocation(): Promise<void>{
    this.cargandoUbicacion.setValue(true);
    try {
      const coords = await this.geolocationService.getLocation();
      this.filterForm.get('userLatitude')?.setValue(coords.latitude);
      this.filterForm.get('userLongitude')?.setValue(coords.longitude);
    } catch (error) {
      this.alerts.error('No pudimos obtener tu ubicación. Por favor permite el acceso.');
      this.userCurrentLocation.setValue(false);
    } finally {
      this.cargandoUbicacion.setValue(false);
    }
  }

  pets:PetResponse[] = [];

  buscarMascotas(): void {
    this.searchTrigger$.next();
  }
  ngOnInit(): void {

    if (this.userCurrentLocation.value) {
        this.loadUserLocation();
    }


    const searchEvents$ = this.searchTrigger$.asObservable().pipe(
        startWith(null)
    );

    const loadingChange$ = this.cargandoUbicacion.valueChanges.pipe(startWith(this.cargandoUbicacion.value));


    combineLatest([searchEvents$, loadingChange$]).pipe(

        switchMap(([_, loading]) => {

            const formValues = this.filterForm.value;
            const useLocation = this.userCurrentLocation.value;
            const lat = formValues.userLatitude;
            const lon = formValues.userLongitude;

            if (loading || (useLocation && (lat === null || lon === null))) {
                return new Observable<PetResponse[]>(observer => {
                    observer.next([]);
                    observer.complete();
                });
            }

            const filters: any = { ...formValues };

            if (!useLocation) {
                delete filters.userLatitude;
                delete filters.userLongitude;
                delete filters.maxDistanceInKm;
            }

            const finalFilters: Record<string, any> = {};

            Object.keys(filters).forEach(key => {
                let value = filters[key];

                if (value !== undefined && value !== null && value !== '') {
                    finalFilters[key] = value;
                }
            });

            return this.petService.listAllPets(finalFilters as PetFilter);
        })
    ).subscribe({
        next: (pets) => {
            this.pets = pets;
        },
        error: (err) => {
             this.alerts.error('Error al cargar la lista de mascotas.');
             console.error(err);
             this.pets = [];
        }
    });

  }
}

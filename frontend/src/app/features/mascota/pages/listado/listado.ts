import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MascotaService } from '../../mascota.service';
import { BehaviorSubject, combineLatest, debounceTime, distinctUntilChanged, EMPTY, filter, Observable, race, startWith, Subject, switchMap } from 'rxjs';

import { GeolocationService } from '../../../../core/services/geolocation.service';
import { AlertService } from '../../../../core/services/alert.service';
import { PetFilter, PetResponse } from '../../mascota.model';

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
    
    this.userCurrentLocation.valueChanges.subscribe((useLocation) => {
      if (useLocation) {
        this.loadUserLocation();
      } else {
        this.filterForm.patchValue({ userLatitude: null, userLongitude: null });

      }
    });
  }

  async loadUserLocation(): Promise<{latitude: number, longitude: number} | null>{
    this.cargandoUbicacion.setValue(true);
    try {
      const timeout = new Promise((_, reject) => setTimeout(() => reject(new Error('Timeout')), 8000));
      
      const coords = await Promise.race([
        this.geolocationService.getLocation(),
        timeout
      ]) as { latitude: number, longitude: number };

      this.filterForm.patchValue({
        userLatitude: coords.latitude,
        userLongitude: coords.longitude
      });
      return coords;
    } catch (error) {
      this.alerts.error('No pudimos obtener tu ubicación automáticamente.');
      this.userCurrentLocation.setValue(false);
      return null;
    } finally {
      this.cargandoUbicacion.setValue(false); 
    }
  }

  pets:PetResponse[] = [];
  busquedaRealizada = true;

  ngOnInit(): void {
  this.searchTrigger$.asObservable().pipe(
    switchMap(() => {
      const rawValues = this.filterForm.getRawValue(); 
      const useLocation = this.userCurrentLocation.value;
      const finalFilters: Record<string, any> = {};

      Object.keys(rawValues).forEach(key => {
          const value = rawValues[key];
          if (value !== undefined && value !== null && value !== '' && value !== 'null') {
              if (!useLocation && ['userLatitude', 'userLongitude', 'maxDistanceInKm'].includes(key)) return;
              finalFilters[key] = value;
          }
      });
      
      console.log("Enviando filtros:", finalFilters);
      return this.petService.listAllPets(finalFilters as PetFilter);
    })
  ).subscribe({ 
    next: (pets) => { 
      this.pets = pets || []; 
      this.busquedaRealizada = true; 
    },
    error: (err) => {
      this.alerts.error('Error al conectar con el servidor.');
      this.pets = [];
    }
  });

  this.searchTrigger$.next();
}

async buscarMascotas(): Promise<void> {
  if (this.userCurrentLocation.value && !this.filterForm.get('userLatitude')?.value) {
    const coords = await this.loadUserLocation();
    if (!coords) return; 
    
    
    setTimeout(() => this.searchTrigger$.next(), 100);
  } else {
    this.searchTrigger$.next();
  }
}
}
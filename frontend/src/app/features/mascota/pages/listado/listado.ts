import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MascotaService } from '../../mascota.service';
import { combineLatest, debounceTime, distinctUntilChanged, Observable, race, startWith, switchMap } from 'rxjs';
import { GeolocationService } from '../../../../services/geolocation.service';
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

  userLatitude=new FormControl<number|null>(null);
  userLongitude=new FormControl<number|null>(null);

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
    });
    
    this.userCurrentLocation.valueChanges.subscribe(
      () => {
        this.loadUserLocation();
      }

    )
  }

  async loadUserLocation(){
    this.cargandoUbicacion.setValue(true);
    try {
      const coords = await this.geolocationService.getLocation();
      this.userLatitude.setValue(coords.latitude);
      this.userLongitude.setValue(coords.longitude);
    } catch (error) {
      this.alerts.error('No pudimos obtener tu ubicación. Por favor permite el acceso.');
      this.userCurrentLocation.setValue(false);
    } finally {
      this.cargandoUbicacion.setValue(false);
    }
  }

  pets: PetResponse[];
  ngOnInit(): void {
    
  }
}


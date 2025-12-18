import { Component, OnInit } from "@angular/core";
import { UserCardComponent } from "../../shared/components/userCard/userCard.component";
import { CarouselComponent } from "../../shared/components/carousel/carousel.component";
import { MarkerInfo, MapComponent } from '../../shared/components/map/map'; // <--- Importante
import { SightingService } from "../../core/services/sigthing.service";
import { SightingResponse } from "../../core/models/sighting.models";

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CarouselComponent, UserCardComponent, MapComponent], // <--- Importante
})
export class HomeComponent implements OnInit {
    sightings: SightingResponse[] = [];
    title = '¿Donde estás? Volvé a casa';
    public sightingsLocationList: MarkerInfo[] = [];

    constructor(private sightingService: SightingService) {}

    ngOnInit() {
        console.log('🏠 HomeComponent ngOnInit - solicitando avistamientos');
        this.sightingService.getAllSightings().subscribe({
            next: (response) => {
                console.log('✅ Avistamientos recibidos:', response.length, 'items');
                this.sightings = response;
                // Mapeo simple de datos
                this.sightingsLocationList = response.map(sighting => ({
                    latitude: sighting.latitude,
                    longitude: sighting.longitude,
                    popupText: `<b>Avistamiento</b><br>${new Date(sighting.date).toLocaleDateString()}`
                }));
                console.log('📍 sightingsLocationList actualizado:', this.sightingsLocationList.length, 'marcadores');
            },
            error: (error) => {
                console.error('❌ Error cargando avistamientos:', error);
                this.sightingsLocationList = [];
            }
        });
    }
}
import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common"; 
import { UserCardComponent } from "../../shared/components/userCard/userCard.component";
import { CarouselComponent } from "../../shared/components/carousel/carousel.component";
import { MarkerInfo, MapComponent } from '../../shared/components/map/map'; 
import { SightingService } from "../../core/services/sigthing.service";
import { UserService } from "../../core/services/user.service";
import { SightingResponse } from "../../core/models/sighting.models";
import { UserProfile } from "../../core/models/user.models"; 

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CommonModule, CarouselComponent, UserCardComponent, MapComponent], 
})
export class HomeComponent implements OnInit {
    sightings: SightingResponse[] = [];
    topUsers: UserProfile[] = []; 
    title = '¿Donde estás? Volvé a casa';
    public sightingsLocationList: MarkerInfo[] = [];

    constructor(
        private sightingService: SightingService,
        private userService: UserService
    ) {}

    ngOnInit() {
        this.loadSightings();
        this.loadTopUsers(); 
    }

    loadSightings() {
        this.sightingService.getAllSightings().subscribe({
            next: (response) => {
                this.sightings = response;
                this.sightingsLocationList = response.map(sighting => ({
                    latitude: sighting.latitude,
                    longitude: sighting.longitude,
                    popupText: `<b>Avistamiento</b><br>${new Date(sighting.date).toLocaleDateString()}`
                }));
            },
            error: (error) => {
                console.error('Error cargando avistamientos:', error);
                this.sightingsLocationList = [];
            }
        });
    }

    loadTopUsers() {
        this.userService.getAllUsersFiltered({}, 0, 5, 'points', 'DESC').subscribe({
            next: (users) => {
                this.topUsers = users;
            },
            error: (err) => {
                console.error('Error cargando Top 5:', err);
            }
        });
    }
}
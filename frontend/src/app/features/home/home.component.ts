import { Component, OnInit, signal, inject } from "@angular/core";
import { CommonModule } from "@angular/common"; 
import { UserCardComponent } from "../../shared/components/userCard/userCard.component";
import { CarouselComponent, CarouselImage } from "../../shared/components/carousel/carousel.component";
import { MarkerInfo, MapComponent } from '../../shared/components/map/map'; 
import { SightingService } from "../../core/services/sigthing.service";
import { UserService } from "../../core/services/user.service";
import { MascotaService } from "../../features/mascota/mascota.service";
import { State } from "../../features/mascota/mascota.model"; 

import { SightingResponse } from "../../core/models/sighting.models";
import { UserProfile } from "../../core/models/user.models";

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CommonModule, CarouselComponent, UserCardComponent, MapComponent], 
})
export class HomeComponent implements OnInit {
    private sightingService = inject(SightingService);
    private userService = inject(UserService);
    private mascotaService = inject(MascotaService);

    sightings = signal<SightingResponse[]>([]);
    sightingsLocationList = signal<MarkerInfo[]>([]);
    topUsers = signal<UserProfile[]>([]);
    
    carouselImages = signal<CarouselImage[]>([]);

    title = '¿Donde estás? Volvé a casa';

    ngOnInit() {
        this.loadSightings();
        this.loadTopUsers();
        this.loadLostPets(); 
    }

    loadLostPets() {
        this.mascotaService.listAllPets({ state: State.PERDIDO_PROPIO }).subscribe({
            next: (pets) => {
                const images: CarouselImage[] = pets
                    .filter(pet => pet.photosBase64 && pet.photosBase64.length > 0)
                    .map(pet => ({
                        src: `data:image/jpeg;base64,${pet.photosBase64[0]}`,
                        alt: `Foto de ${pet.name || 'Mascota perdida'}`
                    }))
                    .slice(0, 10); 

                this.carouselImages.set(images);
            },
            error: (err) => console.error('Error cargando mascotas perdidas:', err)
        });
    }

    loadSightings() {
        this.sightingService.getAllSightings().subscribe({
            next: (response) => {
                this.sightings.set(response);
                
                const locations = response.map(sighting => ({
                    latitude: sighting.latitude,
                    longitude: sighting.longitude,
                    popupText: `<b>Avistamiento</b><br>${new Date(sighting.date).toLocaleDateString()}`
                }));
                
                this.sightingsLocationList.set(locations);
            },
            error: (error) => {
                console.error('Error cargando avistamientos:', error);
                this.sightingsLocationList.set([]);
            }
        });
    }

    loadTopUsers() {
        this.userService.getAllUsersFiltered({}, 0, 5, 'points', 'DESC').subscribe({
            next: (users) => {
                this.topUsers.set(users);
            },
            error: (err) => {
                console.error('Error cargando Top 5:', err);
            }
        });
    }
}
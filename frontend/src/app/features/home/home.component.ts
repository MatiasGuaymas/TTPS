import { Component, OnInit, signal, inject } from "@angular/core";
import { DomSanitizer } from '@angular/platform-browser';
import { CommonModule } from "@angular/common";
import { forkJoin } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserCardComponent } from "../../shared/components/userCard/userCard.component";
import { CarouselComponent, CarouselImage } from "../../shared/components/carousel/carousel.component";
import { MarkerInfo, MapComponent } from '../../shared/components/map/map';
import { SightingService } from "../../core/services/sigthing.service";
import { UserService } from "../../core/services/user.service";
import { MascotaService } from "../../features/mascota/mascota.service";
import { State } from "../../features/mascota/mascota.model";
import { SightingResponse } from "../../core/models/sighting.models";
import { UserProfile } from "../../core/models/user.models";
import { Router } from "@angular/router";

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CommonModule, CarouselComponent, UserCardComponent, MapComponent],
})
export class HomeComponent implements OnInit {
    private sightingService = inject(SightingService);
    private userService = inject(UserService);
    private mascotaService = inject(MascotaService);
    private sanitizer = inject(DomSanitizer);
    private router = inject(Router);

    sightings = signal<SightingResponse[]>([]);
    sightingsLocationList = signal<MarkerInfo[]>([]);
    topUsers = signal<UserProfile[]>([]);
    carouselImages = signal<CarouselImage[]>([]);

    loadingCarousel = signal<boolean>(true);
    loadingMap = signal<boolean>(true);
    loadingRanking = signal<boolean>(true);

    title = '¿Donde estás? Volvé a casa';

    ngOnInit() {
        this.loadSightings();
        this.loadTopUsers();
        this.loadLostPets();
    }

    loadLostPets() {
        this.loadingCarousel.set(true);
        const requestPropios = this.mascotaService.listAllPets({ state: State.PERDIDO_PROPIO }).pipe(
            map(pets => pets || [])
        );
        const requestAjenos = this.mascotaService.listAllPets({ state: State.PERDIDO_AJENO }).pipe(
            map(pets => pets || [])
        );

        forkJoin([requestPropios, requestAjenos])
            .pipe(finalize(() => this.loadingCarousel.set(false)))
            .subscribe({
                next: ([propios, ajenos]) => {
                    const allLostPets = [...propios, ...ajenos];
                    const images = allLostPets
                        .filter(pet => pet.photosBase64 && pet.photosBase64.length > 0)
                        .map(pet => {
                            let photoData = pet.photosBase64[0];
                            if (!photoData.startsWith('data:')) {
                                photoData = `data:image/jpeg;base64,${photoData}`;
                            }
                            return {
                                src: photoData,
                                alt: `Mascota: ${pet.name || 'Sin nombre'}`
                            };
                        })
                        .slice(0, 10);

                    this.carouselImages.set(images);
                },
                error: (err) => console.error(err)
            });
    }

    loadSightings() {
        this.loadingMap.set(true);
        this.sightingService.getAllSightings()
            .pipe(
                map(response => response || []),
                finalize(() => this.loadingMap.set(false))
            )
            .subscribe({
                next: (response) => {
                    this.sightings.set(response);
                    this.sightingsLocationList.set(response.map(s => ({
                        latitude: s.latitude,
                        longitude: s.longitude,
                        popupText: `<b>Avistamiento</b><br>${new Date(s.date).toLocaleDateString()}`
                    })));
                },
                error: (e) => console.error(e)
            });
    }

    loadTopUsers() {
        this.loadingRanking.set(true);
        this.userService.getAllUsersFiltered({}, 0, 5, 'points', 'DESC')
            .pipe(
                map(users => users || []),
                finalize(() => this.loadingRanking.set(false))
            )
            .subscribe({
                next: (users) => this.topUsers.set(users),
                error: (e) => console.error(e)
            });
    }

    goToDetail(index: number) {
        this.router.navigate(['/mascota/', index + 1]);
    }
}

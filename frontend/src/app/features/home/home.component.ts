import { Component } from "@angular/core";
import { UserCardComponent } from "../../shared/components/userCard/userCard.component";
import { CarouselComponent } from "../../shared/components/carousel/carousel.component";
import { MapComponent } from "../../shared/components/map/map.component";
import { SightingService } from "../../core/services/sigthing.service";
import { AlertService } from "../../core/services/alert.service";
import { SightingResponse } from "../../core/models/sighting.models";

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CarouselComponent, UserCardComponent, MapComponent],
})
export class HomeComponent {
    sightings: SightingResponse[] = [];
    title = '¿Donde estás? Volvé a casa';

    constructor(private sightingService: SightingService) {}

    ngOnInit() {
        this.sightingService.getAllSightings().subscribe({
            next: (response) => {
                this.sightings = response;
            },
            error: (error) => {
                this.sightings = [];
            }
        });
    }
}
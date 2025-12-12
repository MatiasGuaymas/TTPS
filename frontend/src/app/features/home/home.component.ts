import { Component } from "@angular/core";
import { UserCardComponent } from "../../shared/components/userCard/userCard.component";
import { CarouselComponent } from "../../shared/components/carousel/carousel.component";

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CarouselComponent, UserCardComponent],
})
export class HomeComponent {
    title = '¿Donde estás? Volvé a casa';
}

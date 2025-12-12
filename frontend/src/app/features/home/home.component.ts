import { Component } from "@angular/core";
import { CarouselComponent } from "../shared/components/carousel/carousel.component";
import { UserCardComponent } from "../shared/components/userCard/userCard.component";

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CarouselComponent, UserCardComponent],
})
export class HomeComponent {
    title = '¿Donde estás? Volvé a casa';
}

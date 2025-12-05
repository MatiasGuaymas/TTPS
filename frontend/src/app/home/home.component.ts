import { Component } from "@angular/core";
import { CarouselComponent } from "../generics/carousel/carousel.component";
import { UserCardComponent } from "../generics/userCard/userCard.component";    

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html',
    imports: [CarouselComponent, UserCardComponent],
})
export class HomeComponent {
    title = '¿Donde estás? Volvé a casa';
}
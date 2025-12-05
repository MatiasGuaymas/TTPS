import { Component } from "@angular/core";

@Component({
    selector: 'carousel',
    templateUrl: 'carousel.component.html',
})
export class CarouselComponent {
    activeIndex = 0;
    intervalId: any;

    // Puedes agregar más imágenes aquí
    images = [
        { src: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRqC8wvz4gS3CIvkKZ-DBq0PbSeV2xn1Nhzo_6Jl5ZHbsQ-EZludTh0kQXbTjsReBzh6H0mKraNUXi4ArEW1DlzMEtFokzNc5RaKuUukno&s=10', alt: 'Imagen 1' },
        { src: 'https://cdn.sanity.io/images/5vm5yn1d/pro/5cb1f9400891d9da5a4926d7814bd1b89127ecba-1300x867.jpg?fm=webp&q=80', alt: 'Imagen 2' },
        { src: 'https://placehold.co/600x400/111/fff?text=Imagen+3', alt: 'Imagen 3' }, // Agregué una 3ra para probar
    ];

    ngOnInit() {
        this.startAutoSlide();
    }

    ngOnDestroy() {
        this.stopAutoSlide();
    }

    // Inicia el cambio automático
    startAutoSlide() {
        this.intervalId = setInterval(() => {
            this.next();
        }, 3000); // Cambia cada 3 segundos (3000ms)
    }

    // Detiene el cambio (útil si el usuario interactúa manualmente)
    stopAutoSlide() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    // Avanzar
    next() {
        this.activeIndex = (this.activeIndex < this.images.length - 1) 
            ? this.activeIndex + 1 
            : 0;
    }

    // Retroceder
    prev() {
        this.activeIndex = (this.activeIndex > 0) 
            ? this.activeIndex - 1 
            : this.images.length - 1;
    }

    // Ir a una diapositiva específica (al hacer click en un punto)
    goTo(index: number) {
        this.activeIndex = index;
        // Opcional: Reiniciar el timer cuando el usuario toca un punto
        this.stopAutoSlide();
        this.startAutoSlide();
    }
}
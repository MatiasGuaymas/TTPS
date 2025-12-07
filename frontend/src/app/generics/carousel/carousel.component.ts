import { Component } from "@angular/core";

@Component({
    selector: 'carousel',
    templateUrl: 'carousel.component.html',
})
export class CarouselComponent {
    activeIndex = 0;
    intervalId: any;

    images = [
        { src: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRqC8wvz4gS3CIvkKZ-DBq0PbSeV2xn1Nhzo_6Jl5ZHbsQ-EZludTh0kQXbTjsReBzh6H0mKraNUXi4ArEW1DlzMEtFokzNc5RaKuUukno&s=10', alt: 'Imagen 1' },
        { src: 'https://cdn.sanity.io/images/5vm5yn1d/pro/5cb1f9400891d9da5a4926d7814bd1b89127ecba-1300x867.jpg?fm=webp&q=80', alt: 'Imagen 2' },
        { src: 'https://placehold.co/600x400/111/fff?text=Imagen+3', alt: 'Imagen 3' },
        { src: 'https://placehold.co/600x400/222/fff?text=Imagen+4', alt: 'Imagen 4' },
        { src: 'https://placehold.co/600x400/333/fff?text=Imagen+5', alt: 'Imagen 5' },
    ];

    ngOnInit() {
        this.startAutoSlide();
    }

    ngOnDestroy() {
        this.stopAutoSlide();
    }

    startAutoSlide() {
        this.intervalId = setInterval(() => {
            // Este next se ejecuta porque cambia el activeIndex
            // pero no se emite ningun evento por ende no se actualiza el HTML
            this.next();
        }, 4000); // Cambia cada 4 segundos
    }

    stopAutoSlide() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    next() {
        this.activeIndex = (this.activeIndex < this.images.length - 1)
            ? this.activeIndex + 1
            : 0;
    }

    prev() {
        this.activeIndex = (this.activeIndex > 0)
            ? this.activeIndex - 1
            : this.images.length - 1;
    }

    goTo(index: number) {
        this.activeIndex = index;
        this.stopAutoSlide();
        this.startAutoSlide();
    }

    // Función que determina las clases CSS según la posición
    getCardClasses(index: number): string {
        const diff = index - this.activeIndex;
        const total = this.images.length;

        // Calcular la distancia circular
        let distance = diff;
        if (Math.abs(diff) > total / 2) {
            distance = diff > 0 ? diff - total : diff + total;
        }

        // Imagen central (activa)
        if (distance === 0) {
            return 'z-30 scale-100 translate-x-0 w-[500px] h-[350px] opacity-100';
        }
        // Imagen a la izquierda
        else if (distance === -1 || (distance === total - 1)) {
            return 'z-20 scale-75 -translate-x-[280px] w-[500px] h-[350px] opacity-60';
        }
        // Imagen a la derecha
        else if (distance === 1 || (distance === -(total - 1))) {
            return 'z-20 scale-75 translate-x-[280px] w-[500px] h-[350px] opacity-60';
        }
        // Imágenes ocultas
        else {
            return 'z-0 scale-50 opacity-0 w-[500px] h-[350px]';
        }
    }
}

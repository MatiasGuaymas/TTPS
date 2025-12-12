import { Component, OnDestroy, OnInit, signal } from "@angular/core";
import { NgClass } from '@angular/common';

@Component({
    selector: 'carousel',
    imports: [NgClass],
    templateUrl: 'carousel.component.html',
})
export class CarouselComponent implements OnInit, OnDestroy {
    activeIndex = signal(0);
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
        this.stopAutoSlide(); 
        this.intervalId = setInterval(() => {
            this.next(); 
        }, 4000); 
    }
    
    stopAutoSlide() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
            this.intervalId = null; 
        }
    }
    
    resetTimer() {
        this.stopAutoSlide();
        this.startAutoSlide();
    }

    next() {
        this.activeIndex.update(i =>
            i < this.images.length - 1 ? i + 1 : 0
        );
        this.resetTimer();
    }

    prev() {
        this.activeIndex.update(i =>
            i > 0 ? i - 1 : this.images.length - 1
        );
        this.resetTimer();
    }

    goTo(index: number) {
        this.activeIndex.set(index);
        this.resetTimer();
    }

    getCardClasses(index: number): string {
        const diff = index - this.activeIndex(); 
        const total = this.images.length;

        let distance = diff;
        if (Math.abs(diff) > total / 2) {
            distance = diff > 0 ? diff - total : diff + total;
        }
        const baseSize = 'w-[280px] h-[280px] md:w-[500px] md:h-[350px]';
        
        const translateLeft = '-translate-x-[60px] md:-translate-x-[280px]';
        const translateRight = 'translate-x-[60px] md:translate-x-[280px]';

        if (distance === 0) {
            return `z-20 scale-100 translate-x-0 opacity-100 ${baseSize}`;
        } 
        
        else if (distance === -1 || (distance === total - 1)) {
            return `z-10 scale-75 ${translateLeft} opacity-60 ${baseSize}`;
        } 
        
        else if (distance === 1 || (distance === -(total - 1))) {
            return `z-10 scale-75 ${translateRight} opacity-60 ${baseSize}`;
        } 
        
        else {
            return `z-0 scale-50 opacity-0 ${baseSize}`;
        }
    }
}
import { Component, OnDestroy, OnInit, signal, Input, OnChanges, SimpleChanges } from "@angular/core";
import { NgClass, NgIf } from '@angular/common';

export interface CarouselImage {
  src: string;
  alt: string;
}

@Component({
    selector: 'carousel',
    imports: [NgClass, NgIf],
    templateUrl: 'carousel.component.html',
})
export class CarouselComponent implements OnInit, OnDestroy, OnChanges {
    activeIndex = signal(0);
    intervalId: any;

    @Input() images: CarouselImage[] = [];

    ngOnInit() {
        this.startAutoSlide();
    }
    
    
    ngOnChanges(changes: SimpleChanges): void {
        if (changes['images'] && !changes['images'].firstChange) {
            this.resetTimer();
        }
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
        if (this.images.length === 0) return; 
        this.activeIndex.update(i =>
            i < this.images.length - 1 ? i + 1 : 0
        );
        this.resetTimer();
    }

    prev() {
        if (this.images.length === 0) return; 
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
        if (this.images.length === 0) return '';
        
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
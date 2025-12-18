import { Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges, ViewChild, AfterViewInit } from "@angular/core";
import { CommonModule } from "@angular/common"; // Importar CommonModule para usar pipes en el HTML
import * as L from 'leaflet';

@Component({
  selector: "leaflet-map",
  standalone: true,
  imports: [CommonModule], 
  templateUrl: "./map.component.html", // Asegurate que la ruta sea correcta
  styles: [`
    #map { height: 100%; width: 100%; }
  `]
})
export class MapComponent implements AfterViewInit, OnChanges, OnDestroy {

  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef<HTMLDivElement>;

  @Input() lat: number = -34.6037;
  @Input() lng: number = -58.3816;

  @Output() locationChange = new EventEmitter<{lat: number, lng: number}>();

  private map!: L.Map;
  private marker!: L.Marker;

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.map && this.marker) {
      // Si cambian las coordenadas desde el padre
      if (changes['lat'] || changes['lng']) {
        const newLat = changes['lat'] ? changes['lat'].currentValue : this.lat;
        const newLng = changes['lng'] ? changes['lng'].currentValue : this.lng;
        
        this.marker.setLatLng([newLat, newLng]);
        this.map.setView([newLat, newLng], 13); // Usar setView para centrar también
      }
    }
  }

  private initMap(): void {
    if (!this.mapContainer) return;

    // Fix iconos Leaflet
    delete (L.Icon.Default.prototype as any)._getIconUrl;
    L.Icon.Default.mergeOptions({
      iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
      iconUrl: 'assets/leaflet/marker-icon.png',
      shadowUrl: 'assets/leaflet/marker-shadow.png',
    });

    this.map = L.map(this.mapContainer.nativeElement).setView([this.lat, this.lng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.marker = L.marker([this.lat, this.lng], { draggable: true }).addTo(this.map);

    // Cuando sueltas el marker (drag)
    this.marker.on('dragend', () => {
      const pos = this.marker.getLatLng();
      this.emitLocation(pos.lat, pos.lng);
    });

    // Cuando haces click en el mapa
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.marker.setLatLng(e.latlng);
      this.emitLocation(e.latlng.lat, e.latlng.lng);
    });
    
    // Forzar redibujado por si el contenedor estaba oculto o cambio de tamaño
    setTimeout(() => {
        this.map.invalidateSize();
    }, 100);
  }

  private emitLocation(lat: number, lng: number) {
    this.locationChange.emit({ lat, lng });
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }
}
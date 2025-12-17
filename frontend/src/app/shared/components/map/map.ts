import { AfterViewInit, Component, EventEmitter, OnInit, Output, Input, ElementRef, ViewChild } from '@angular/core';
import * as L from 'leaflet';
import { Coordinates } from '../../../core/models/coordinates';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements OnInit, AfterViewInit {

  private map: any;
  private userMarker: L.Marker<any> | null = null;

  @Output() coordinatesChanged = new EventEmitter<{ latitude: number, longitude: number }>();
  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef;

  @Input() initialLatitude?: number;
  @Input() initialLongitude?: number;
  @Input() readonly: boolean = false;
  @Input() popupText: string = 'Ubicación de la mascota (Arrastrable)';
  @Input() zoom: number = 13;

  private readonly INITIAL_LATITUDE = -34.6037;
  private readonly INITIAL_LONGITUDE = -58.3816;

  ngOnInit(): void {

  }


  ngAfterViewInit(): void {
    setTimeout(() => {
      this.initMap();
      const lat = this.initialLatitude ?? this.INITIAL_LATITUDE;
      const lng = this.initialLongitude ?? this.INITIAL_LONGITUDE;
      this.placeInitialMarker(lat, lng);
    }, 100);
  }

  private initMap() {
    if (!this.mapContainer?.nativeElement) return;

    const lat = this.initialLatitude ?? this.INITIAL_LATITUDE;
    const lng = this.initialLongitude ?? this.INITIAL_LONGITUDE;

    this.map = L.map(this.mapContainer.nativeElement).setView([lat, lng], this.zoom);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

  }

  private placeInitialMarker(lat: number, lng: number): void {
    const coords: L.LatLngTuple = [lat, lng];

    const myIcon = L.icon({
      iconUrl: 'assets/leaflet/marker-icon-2x.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34]
    });

    this.userMarker = L.marker(coords, {
      icon: myIcon,
      draggable: !this.readonly
    }).addTo(this.map)
      .bindPopup(this.popupText)
      .openPopup();

    if (!this.readonly) {
      this.userMarker.on('dragend', (event) => {
        const marker = event.target;
        const position = marker.getLatLng();

        this.coordinatesChanged.emit({
          latitude: position.lat,
          longitude: position.lng
        });
      });

      this.map.on('click', (e: L.LeafletMouseEvent) => {
        this.userMarker!.setLatLng(e.latlng);
        this.coordinatesChanged.emit({
          latitude: e.latlng.lat,
          longitude: e.latlng.lng
        });
      });
    }

    this.coordinatesChanged.emit({
      latitude: lat,
      longitude: lng
    });
  }
}
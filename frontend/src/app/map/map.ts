import { AfterViewInit, Component, EventEmitter, OnInit, Output } from '@angular/core';
import * as L from 'leaflet';
import { Coordinates } from '../models/coordinates';


@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements OnInit,AfterViewInit{

  private map:any;
  private userMarker:L.Marker<any>|null=null;

  @Output() coordinatesChanged = new EventEmitter<{ latitude: number, longitude: number }>(); 
  

  private readonly INITIAL_LATITUDE = -34.6037; 
  private readonly INITIAL_LONGITUDE = -58.3816;

  ngOnInit(): void {
    
  }


  ngAfterViewInit(): void { 
    this.initMap();
    this.placeInitialMarker(this.INITIAL_LATITUDE, this.INITIAL_LONGITUDE);
  }
  private initMap() {
    this.map = L.map('map').setView([this.INITIAL_LATITUDE, this.INITIAL_LONGITUDE], 13);
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
      draggable: true 
    }).addTo(this.map)
      .bindPopup("Ubicación de la mascota (Arrastrable)")
      .openPopup();

    this.userMarker.on('dragend', (event) => {
        const marker = event.target;
        const position = marker.getLatLng();
        
        this.coordinatesChanged.emit({ 
            latitude: position.lat, 
            longitude: position.lng 
        });
    });

    this.coordinatesChanged.emit({ 
        latitude: lat, 
        longitude: lng 
    });

    this.map.on('click', (e: L.LeafletMouseEvent) => {
        this.userMarker!.setLatLng(e.latlng);
        this.coordinatesChanged.emit({
            latitude: e.latlng.lat,
            longitude: e.latlng.lng
        });
    });
  }
}
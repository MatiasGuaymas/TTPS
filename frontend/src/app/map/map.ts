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
  
  ngOnInit(): void {
    
  }


  ngAfterViewInit(): void { 
    this.initMap();
    this.getLoc();
  }
  private initMap() {
    this.map = L.map('map').setView([-34.6037, -58.3816], 13); 

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

  }

  getLoc(): void {
    if (navigator.geolocation) {

        const myIcon=L.icon({
          iconUrl:'assets/leaflet/marker-icon-2x.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41], 
          popupAnchor: [1, -34]
        });
        navigator.geolocation.getCurrentPosition(
            (position) => {
              const { latitude, longitude } = position.coords;
              const coords: L.LatLngTuple = [latitude, longitude];
              

              if (!this.userMarker) {
                this.userMarker = L.marker(coords, {
                  icon: myIcon,
                  draggable: true 
                }).addTo(this.map)
                  .bindPopup("Ubicación de la mascota")
                  .openPopup();

                this.userMarker.on('dragend', (event) => {
                    const marker = event.target;
                    const position = marker.getLatLng();
                    
                    this.coordinatesChanged.emit({ 
                        latitude: position.lat, 
                        longitude: position.lng 
                    });
                });
              } else {
                this.userMarker.setLatLng(coords);
              }
              
              this.map.setView(coords, 16); 
              
              this.coordinatesChanged.emit({ 
                  latitude: latitude, 
                  longitude: longitude 
              });

            },
            
            () => {
              this.coordinatesChanged.emit({ 
                  latitude: -34.6037, 
                  longitude: -58.3816
              });
              alert("No se pudo obtener la ubicación. Usando Buenos Aires como predeterminado.");
            },
            { enableHighAccuracy: true, timeout: 5000, maximumAge: 0 } // Opciones
      );

    } else {
        alert("Geolocalización no soportada por el navegador. Usando Buenos Aires como predeterminado.");
        this.coordinatesChanged.emit({ 
            latitude: -34.6037,
            longitude: -58.3816
        });
    }
  }
}
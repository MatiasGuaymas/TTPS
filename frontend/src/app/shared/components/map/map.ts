import { AfterViewInit, Component, EventEmitter, Input, OnChanges, SimpleChanges, Output, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import * as L from 'leaflet';

export interface MarkerInfo {
  latitude: number;
  longitude: number;
  popupText?: string;
}

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class MapComponent implements AfterViewInit, OnChanges, OnDestroy {

  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef;
  @Output() coordinatesChanged = new EventEmitter<{ latitude: number, longitude: number }>();

  @Input() initialLatitude: number = -34.6037;
  @Input() initialLongitude: number = -58.3816;
  @Input() readonly: boolean = false;
  @Input() popupText: string = 'Ubicación de la mascota';
  @Input() zoom: number = 13;
  @Input() showDefaultMarker: boolean = true;
  
  private _markersList: MarkerInfo[] = [];
  @Input() 
  set markersList(value: MarkerInfo[]) {
    console.log('markersList setter llamado con:', value?.length, 'marcadores');
    this._markersList = value || [];
    if (this.map) {
      console.log('Mapa existe, refrescando marcadores inmediatamente');
      this.refreshMarkers();
    } else {
      console.log('Mapa no existe aún, marcando pendingRefresh');
      this.pendingRefresh = true;
    }
  }
  get markersList(): MarkerInfo[] {
    return this._markersList;
  }

  private map: L.Map | undefined;
  private markersLayer: L.LayerGroup | null = null;
  private userMarker: L.Marker | null = null;
  private resizeObserver: ResizeObserver | null = null;
  private pendingRefresh = false;

  constructor() {
    this.fixLeafletIcons();
  }

  private fixLeafletIcons() {
    const iconRetinaUrl = '/assets/leaflet/marker-icon-2x.png';
    const iconUrl = '/assets/leaflet/marker-icon.png';
    const shadowUrl = '/assets/leaflet/marker-shadow.png';
    
    const iconDefault = L.icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
    
    L.Marker.prototype.options.icon = iconDefault;
  }

  ngAfterViewInit(): void {
    console.log('ngAfterViewInit - iniciando mapa en 100ms');
    setTimeout(() => {
      console.log('imeout completado, inicializando mapa');
      this.initMap();
      this.setupResizeObserver();
      if (this.pendingRefresh) {
        console.log('PendingRefresh activo, refrescando marcadores');
        this.refreshMarkers();
        this.pendingRefresh = false;
      }
    }, 100);
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('🔶 ngOnChanges llamado:', Object.keys(changes));
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    this.map?.remove();
  }

  private initMap() {
    if (!this.mapContainer?.nativeElement) return;

    console.log('🗺️ Inicializando mapa con', this.markersList.length, 'marcadores en memoria');
    
    let centerLat = this.initialLatitude;
    let centerLng = this.initialLongitude;

    if (this.markersList.length > 0) {
       const lat = Number(this.markersList[0].latitude);
       const lng = Number(this.markersList[0].longitude);
       if(!isNaN(lat)) { centerLat = lat; centerLng = lng; }
    }

    this.map = L.map(this.mapContainer.nativeElement).setView([centerLat, centerLng], this.zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; OpenStreetMap'
    }).addTo(this.map);

    this.markersLayer = L.layerGroup().addTo(this.map);

    this.refreshMarkers();
  }

  private refreshMarkers(): void {
    if (!this.map || !this.markersLayer) return;

    console.log('🔄 refreshMarkers llamado con', this.markersList.length, 'marcadores');
    this.map.invalidateSize();

    this.markersLayer.clearLayers();
    if (this.userMarker) {
        this.map.removeLayer(this.userMarker);
        this.userMarker = null;
    }

    if (this.markersList && this.markersList.length > 0) {
        const bounds = L.latLngBounds([]);
        let hasValid = false;

        this.markersList.forEach(m => {
            const lat = Number(m.latitude);
            const lng = Number(m.longitude);
            if (!isNaN(lat) && !isNaN(lng)) {
                const marker = L.marker([lat, lng], { draggable: false });
                
                if (m.popupText) marker.bindPopup(m.popupText);
                
                this.markersLayer?.addLayer(marker);
                bounds.extend([lat, lng]);
                hasValid = true;
            }
        });

        if (hasValid) {
            setTimeout(() => {
                this.map?.invalidateSize();
                this.map?.fitBounds(bounds, { padding: [50, 50] });
            }, 100);
        }

    } 
    else if (this.showDefaultMarker) {
        this.placeSingleMarker(this.initialLatitude, this.initialLongitude);
    }
  }

  private placeSingleMarker(lat: number, lng: number): void {
    if (!this.map) return;
    
    this.userMarker = L.marker([lat, lng], { draggable: !this.readonly }).addTo(this.map);

    if (this.popupText) this.userMarker.bindPopup(this.popupText);

    if (!this.readonly) {
        this.userMarker.on('dragend', (e) => {
            const { lat, lng } = e.target.getLatLng();
            this.coordinatesChanged.emit({ latitude: lat, longitude: lng });
        });
        this.map.on('click', (e) => {
            this.userMarker?.setLatLng(e.latlng);
            this.coordinatesChanged.emit({ latitude: e.latlng.lat, longitude: e.latlng.lng });
        });
    }
  }

  private setupResizeObserver() {
    if (this.mapContainer?.nativeElement) {
      this.resizeObserver = new ResizeObserver(() => {
        this.map?.invalidateSize();
      });
      this.resizeObserver.observe(this.mapContainer.nativeElement);
    }
  }
}
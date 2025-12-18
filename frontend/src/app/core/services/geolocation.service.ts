import { Injectable } from "@angular/core";
import { Coordinates } from "../models/coordinates";

@Injectable({
  providedIn: 'root'
})

export class GeolocationService {


getLocation():Promise<Coordinates> {
        return new Promise((resolve, reject) => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;
                        resolve({ latitude: lat, longitude: lng });
                    },
                    (error) => {
                        reject("Error al obtener la ubicación: " + error.message);
                    }
            );
            } else {
                reject("Geolocalización no soportada por el navegador.");
            }
        });
    }
}

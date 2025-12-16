import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PetCreate, PetFilter, PetResponse } from "./mascota.model";

@Injectable({
  providedIn: 'root'
})
export class MascotaService {
    private apiUrl = '/api/pets';

    constructor(private http: HttpClient) { }


    crearMascota(petDto: PetCreate, token: string): Observable<any> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'token': token
        });

        return this.http.post(this.apiUrl, petDto, { headers });
    }

    listAllPets(filters:PetFilter): Observable<PetResponse[]> {
        let params = new HttpParams();
        Object.keys(filters).forEach(key => {
            const value = filters[key as keyof PetFilter];
            if(value !== undefined && value !== null) {
                params= params.set(key, value.toString());
            }
        });
        return this.http.get<PetResponse[]>(this.apiUrl, { params });
    }
}

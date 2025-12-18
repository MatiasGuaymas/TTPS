import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PetCreate, PetFilter, PetResponse } from "./mascota.model";

@Injectable({
    providedIn: 'root'
})
export class MascotaService {
    private http = inject(HttpClient);
    private apiUrl = '/api/pets';

    constructor() { }

    crearMascota(petDto: PetCreate, token: string): Observable<any> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'token': token
        });

        return this.http.post("http://localhost:8080/api/pets", petDto, { headers });
    }

    listAllPets(filters:PetFilter): Observable<PetResponse[]> {
        let params = new HttpParams();
        Object.keys(filters).forEach(key => {
            const value = filters[key as keyof PetFilter];
            if(value !== undefined && value !== null) {
                params= params.set(key, value.toString());
            }
        });
        return this.http.get<PetResponse[]>("http://localhost:8080/api/pets", { params });
    }

    getPetById(id: number): Observable<PetResponse> {
        return this.http.get<PetResponse>(`${this.apiUrl}/${id}`);
    }
}

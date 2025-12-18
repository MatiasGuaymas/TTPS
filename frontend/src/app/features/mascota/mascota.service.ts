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

        return this.http.post(`${this.apiUrl}`, petDto, { headers });
    }

    listAllPets(filters: PetFilter = {}, page: number = 0, size: number = 10, sort: string = 'name', direction: 'ASC' | 'DESC' = 'DESC'): Observable<PetResponse[]> {
        const params: any = {
            page: page.toString(),
            size: size.toString(),
            sort: `${sort},${direction}`
        };

        // Filtros
        if (filters.name?.trim()) params.name = filters.name.trim();
        if (filters.state?.trim()) params.state = filters.state.trim();
        if (filters.type?.trim()) params.type = filters.type.trim();
        if (filters.size?.trim()) params.size = filters.size.trim();
        if (filters.color?.trim()) params.color = filters.color.trim();
        if (filters.race?.trim()) params.race = filters.race.trim();

        if (filters.weightMin !== undefined && filters.weightMin !== null) params.weightMin = filters.weightMin.toString();
        if (filters.weightMax !== undefined && filters.weightMax !== null) params.weightMax = filters.weightMax.toString();
        if (filters.userLatitude !== undefined && filters.userLatitude !== null) params.userLatitude = filters.userLatitude.toString();
        if (filters.userLongitude !== undefined && filters.userLongitude !== null) params.userLongitude = filters.userLongitude.toString();
        if (filters.maxDistanceInKm !== undefined) {params.maxDistanceInKm = filters.maxDistanceInKm.toString();}

        if (filters.initialLostDate) params.initialLostDate = filters.initialLostDate;
        if (filters.finalLostDate) params.finalLostDate = filters.finalLostDate;

        return this.http.get<PetResponse[]>(this.apiUrl, { params });
    }

    getPetById(id: number): Observable<PetResponse> {
        return this.http.get<PetResponse>(`${this.apiUrl}/${id}`);
    }
    updatePet(id: number, petDto: any, token: string): Observable<any> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'token': token
        });

        return this.http.put(`${this.apiUrl}/${id}`, petDto, { headers });
    }

    deletePet(id: number, token: string): Observable<any> {
        const headers = new HttpHeaders({
            'token': token
        });
        
        return this.http.delete(`${this.apiUrl}/${id}`, { headers });
    }
}
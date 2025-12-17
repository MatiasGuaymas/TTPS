import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AvistamientoService {
    private http = inject(HttpClient);
    private apiUrl = '/api/sightings';

    createSighting(sighting: SightingCreate): Observable<SightingResponse> {
        return this.http.post<SightingResponse>(this.apiUrl, sighting);
    }

    getAllSightings(): Observable<SightingResponse[]> {
        return this.http.get<SightingResponse[]>(this.apiUrl);
    }

    getSightingById(id: number): Observable<SightingResponse> {
        return this.http.get<SightingResponse>(`${this.apiUrl}/${id}`);
    }

    getSightingsByPetId(petId: number): Observable<SightingResponse[]> {
        return this.http.get<SightingResponse[]>(`${this.apiUrl}/pet/${petId}`);
    }
}

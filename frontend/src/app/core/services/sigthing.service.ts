import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SightingResponse, SightingCreate } from '../models/sighting.models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SightingService {
  private apiUrl = "/api/sightings";

  constructor(private http: HttpClient) { }

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

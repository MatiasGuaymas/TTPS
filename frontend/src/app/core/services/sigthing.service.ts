import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SightingResponse } from '../models/sighting.models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SightingService {
    private apiUrl = "/api/sightings";
    
    constructor(private http: HttpClient) { }

    getAllSightings(): Observable<SightingResponse[]> {
        return this.http.get<SightingResponse[]>(this.apiUrl);
    }
}

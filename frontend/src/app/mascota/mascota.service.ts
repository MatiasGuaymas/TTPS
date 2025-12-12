import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PetCreate } from "./mascota.model";

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
       
        return this.http.post(this.apiUrl, petDto, { headers });    }
}
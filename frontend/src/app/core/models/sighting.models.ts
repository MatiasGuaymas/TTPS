export interface SightingCreate {
    petId: number;
    latitude: number;
    longitude: number;
    photoBase64?: string;
    date: string;
    comment?: string;
}

export interface SightingResponse {
    id: number;
    petId: number;
    reporterId: number;
    reporterName: string;
    reporterLastName: string;
    date: string;
    latitude: number;
    longitude: number;
    photoBase64: string;
    comment?: string;
}
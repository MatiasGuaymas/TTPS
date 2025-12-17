interface SightingCreate {
    petId: number;
    latitude: number;
    longitude: number;
    photoBase64?: string;
    date: string; // formato ISO: YYYY-MM-DD
    comment?: string;
}

interface SightingResponse {
    id: number;
    petId: number;
    reporterId: number; reporterName: string;
    reporterLastName: string; latitude: number;
    longitude: number;
    date: string;
    comment?: string;
}

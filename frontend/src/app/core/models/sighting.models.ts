export interface SightingResponse {
    id: number;
    petId: number;
    reporterId: number;
    date: string;
    latitude: number;
    longitude: number;
    photo: string;
    comment: string;
}
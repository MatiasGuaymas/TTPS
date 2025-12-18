import { PetResponse } from "../../features/mascota/mascota.model";

export interface UserProfile {
    id: number;
    name: string;
    lastName: string;
    email: string;
    phone: string;
    city: string;
    neighborhood: string;
    latitude: number;
    longitude: number;
    points: number;
    enabled: boolean;
    role: 'USER' | 'ADMIN';
}

export interface UserUpdateRequest {
    name?: string;
    lastName?: string;
    phoneNumber?: string;
    city?: string;
    neighborhood?: string;
    latitude?: number;
    longitude?: number;
}

export interface AdminUserUpdateRequest extends UserUpdateRequest {
    role?: 'USER' | 'ADMIN';
}

export interface UserFilter {
    email?: string;
    name?: string;
    lastName?: string;
    city?: string;
    neighborhood?: string;
    minPoints?: number;
    maxPoints?: number;
    role?: 'USER' | 'ADMIN';
}

export interface UserPublicProfile {
    id: number;
    name: string;
    lastName: string;
    phone: string;
    city: string;
    neighborhood: string;
    points: number;
    pets: PetResponse[];
}

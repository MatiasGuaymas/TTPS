interface UserProfile {
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

interface UserUpdateRequest {
    name?: string;
    lastName?: string;
    phoneNumber?: string;
    city?: string;
    neighborhood?: string;
    latitude?: number;
    longitude?: number;
}

interface AdminUserUpdateRequest extends UserUpdateRequest {
    role?: 'USER' | 'ADMIN';
}

interface UserFilter {
    email?: string;
    name?: string;
    lastName?: string;
    city?: string;
    neighborhood?: string;
    minPoints?: number;
    maxPoints?: number;
    role?: 'USER' | 'ADMIN';
}

interface UserPublicProfile {
    id: number;
    name: string;
    lastName: string;
    phone: string;
    city: string;
    neighborhood: string;
    pets: PetResponse[];
}

// TODO: PetResponse ya existe en otro lado, habria que cambiar esto.

interface PetResponse {
    id: number;
    name: string;
    size: string;
    description: string;
    color: string;
    race: string;
    weight: number;
    latitude: number;
    longitude: number;
    lostDate: string;
    state: string;
    type: string;
    creatorId: number;
    photosBase64: string[];
}

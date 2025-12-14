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

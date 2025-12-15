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

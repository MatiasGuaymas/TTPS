import { FormControl } from "@angular/forms";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  lastName: string;
  phoneNumber: string;
  latitude: number;
  longitude: number;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    name: string;
    email: string;
    role: string;
  };
}

export type RegisterFormContent = {
  [K in keyof RegisterRequest]: FormControl<RegisterRequest[K]>;
};

export type LoginFormContent = {
  [K in keyof LoginRequest]: FormControl<LoginRequest[K]>;
};

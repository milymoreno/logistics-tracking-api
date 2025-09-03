export interface User {
  id: string;
  email: string;
  hashedPassword: string;
  permissions: string[];
  isActive: boolean;
  lastLogin: Date | null;
  createdAt: Date;
}

export enum UserRole {
  ADMIN = 'admin',
  MANAGER = 'manager',
  OPERATOR = 'operator',
  CUSTOMER = 'customer'
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role?: UserRole;
  permissions?: string[];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  user: Omit<User, 'hashedPassword'>;
  token: string;
}
import { User, LoginRequest, AuthResponse, CreateUserRequest } from '../entities/User';

export interface AuthService {
  login(credentials: LoginRequest): Promise<AuthResponse>;
  register(userData: CreateUserRequest): Promise<AuthResponse>;
  validateToken(token: string): Promise<User | null>;
  refreshToken(token: string): Promise<string>;
}
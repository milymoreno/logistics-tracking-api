import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';
import { AuthService } from '../../domain/services/AuthService';
import { UserRepository } from '../../domain/repositories/UserRepository';
import { User, LoginRequest, AuthResponse, CreateUserRequest } from '../../domain/entities/User';
import { config } from '../../config';

export class JwtAuthService implements AuthService {
  constructor(private userRepository: UserRepository) {}

  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const user = await this.userRepository.findByEmail(credentials.email);
    
    if (!user) {
      throw new Error('Invalid credentials');
    }
    
    if (!user.isActive) {
      throw new Error('User account is disabled');
    }
    
    const isPasswordValid = await bcrypt.compare(credentials.password, user.password);
    
    if (!isPasswordValid) {
      throw new Error('Invalid credentials');
    }
    
    const token = this.generateToken(user);
    
    return {
      user: this.sanitizeUser(user),
      token
    };
  }

  async register(userData: CreateUserRequest): Promise<AuthResponse> {
    // Check if user already exists
    const existingUser = await this.userRepository.findByEmail(userData.email);
    
    if (existingUser) {
      throw new Error('User already exists with this email');
    }
    
    const user = await this.userRepository.create(userData);
    const token = this.generateToken(user);
    
    return {
      user: this.sanitizeUser(user),
      token
    };
  }

  async validateToken(token: string): Promise<User | null> {
    try {
      const decoded = jwt.verify(token, config.jwt.secret) as any;
      const user = await this.userRepository.findById(decoded.userId);
      
      if (!user || !user.isActive) {
        return null;
      }
      
      return user;
    } catch (error) {
      return null;
    }
  }

  async refreshToken(token: string): Promise<string> {
    const user = await this.validateToken(token);
    
    if (!user) {
      throw new Error('Invalid token');
    }
    
    return this.generateToken(user);
  }

  private generateToken(user: User): string {
    const payload = {
      userId: user.id,
      email: user.email,
      role: user.role
    };
    
    return jwt.sign(payload, config.jwt.secret, {
      expiresIn: config.jwt.expiresIn
    });
  }

  private sanitizeUser(user: User): Omit<User, 'password'> {
    const { password, ...sanitizedUser } = user;
    return sanitizedUser;
  }
}
import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';
import { AuthService } from '../../domain/services/AuthService';
import { UserRepository } from '../../domain/repositories/UserRepository';
import { User, LoginRequest, AuthResponse, CreateUserRequest } from '../../domain/entities/User';
import { config } from '../../config';

export class JwtAuthService implements AuthService {
  constructor(private readonly userRepository: UserRepository) {}

  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const user = await this.userRepository.findByEmail(credentials.email);

    if (!user) {
      throw new Error('Invalid credentials');
    }

    if (!user.isActive) {
      throw new Error('User account is disabled');
    }

    const isPasswordValid = await bcrypt.compare(credentials.password, user.hashedPassword);

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
      const jwtSecret = config.jwt.secret;
      if (!jwtSecret) throw new Error('JWT secret is undefined');
      const decoded = jwt.verify(token, jwtSecret) as any;
      const user = await this.userRepository.findById(decoded.userId);

      if (!user?.isActive) {
        return null;
      }

      return user;
    } catch (error) {
      console.error('Token validation error:', error);
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
    const signOptions: jwt.SignOptions = {
      expiresIn: config.jwt.expiresIn ? parseInt(config.jwt.expiresIn) : 86400
    };

    const token = jwt.sign(
      { userId: user.id, email: user.email },
      String(config.jwt.secret),
      signOptions
    );

    return token;
  }

  private sanitizeUser(user: User): Omit<User, 'hashedPassword'> {
    const { hashedPassword, ...sanitizedUser } = user;
    return sanitizedUser;
  }
}
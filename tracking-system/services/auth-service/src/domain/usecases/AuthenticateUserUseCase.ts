import { UserRepository } from '../repositories/UserRepository';
import { TokenGenerator, PasswordHasher } from '../services/TokenGenerator';
import { AuthResult } from '../entities/TokenPayload';

export interface LoginCredentials {
  email: string;
  password: string;
}

export class InvalidCredentialsError extends Error {
  constructor() {
    super('Invalid email or password');
    this.name = 'InvalidCredentialsError';
  }
}

export class UserNotActiveError extends Error {
  constructor() {
    super('User account is not active');
    this.name = 'UserNotActiveError';
  }
}

export class AuthenticateUserUseCase {
  constructor(
    private userRepository: UserRepository,
    private tokenGenerator: TokenGenerator,
    private passwordHasher: PasswordHasher
  ) {}

  async execute(credentials: LoginCredentials): Promise<AuthResult> {
    const user = await this.userRepository.findByEmail(credentials.email);
    
    if (!user) {
      throw new InvalidCredentialsError();
    }

    if (!user.isActive) {
      throw new UserNotActiveError();
    }

    const isPasswordValid = await this.passwordHasher.verify(
      credentials.password, 
      user.hashedPassword
    );

    if (!isPasswordValid) {
      throw new InvalidCredentialsError();
    }

    const token = await this.tokenGenerator.generateToken({
      sub: user.id,
      email: user.email,
      scope: user.permissions
    });

    // Update last login
    await this.userRepository.updateLastLogin(user.id);

    return {
      token,
      user: {
        id: user.id,
        email: user.email,
        permissions: user.permissions
      },
      expiresIn: 3600 // 1 hour
    };
  }
}
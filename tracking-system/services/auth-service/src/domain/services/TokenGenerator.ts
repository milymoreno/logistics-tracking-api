import { TokenPayload, AuthResult } from '../entities/TokenPayload';

export interface TokenGenerator {
  generateToken(payload: TokenPayload): Promise<string>;
  validateToken(token: string): Promise<boolean>;
  decodeToken(token: string): Promise<TokenPayload | null>;
}

export interface PasswordHasher {
  hash(password: string): Promise<string>;
  verify(password: string, hashedPassword: string): Promise<boolean>;
}
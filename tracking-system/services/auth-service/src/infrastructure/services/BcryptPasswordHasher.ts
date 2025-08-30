import bcrypt from 'bcrypt';
import { PasswordHasher } from '../../domain/services/TokenGenerator';

export class BcryptPasswordHasher implements PasswordHasher {
  private readonly saltRounds = 12;

  async hash(password: string): Promise<string> {
    return bcrypt.hash(password, this.saltRounds);
  }

  async verify(password: string, hashedPassword: string): Promise<boolean> {
    return bcrypt.compare(password, hashedPassword);
  }
}
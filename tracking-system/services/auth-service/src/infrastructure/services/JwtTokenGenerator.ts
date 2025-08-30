import jwt from 'jsonwebtoken';
import { TokenGenerator } from '../../domain/services/TokenGenerator';
import { TokenPayload } from '../../domain/entities/TokenPayload';
import { KeyProvider } from '../../domain/repositories/KeyProvider';

export interface JwtConfig {
  issuer: string;
  audience: string;
  expiresIn: string;
  algorithm: 'RS256';
}

export class JwtTokenGenerator implements TokenGenerator {
  constructor(
    private keyProvider: KeyProvider,
    private config: JwtConfig
  ) {}

  async generateToken(payload: TokenPayload): Promise<string> {
    const privateKey = await this.keyProvider.getPrivateKey();
    const keyId = await this.keyProvider.getKeyId();

    const now = Math.floor(Date.now() / 1000);
    
    const jwtPayload = {
      sub: payload.sub,
      email: payload.email,
      scope: payload.scope,
      iss: this.config.issuer,
      aud: this.config.audience,
      iat: now,
      nbf: now,
      exp: now + 3600 // 1 hour
    };

    return jwt.sign(jwtPayload, privateKey, {
      algorithm: this.config.algorithm,
      keyid: keyId
    });
  }

  async validateToken(token: string): Promise<boolean> {
    try {
      const publicKey = await this.keyProvider.getPublicKey();
      
      jwt.verify(token, publicKey, {
        algorithms: [this.config.algorithm],
        issuer: this.config.issuer,
        audience: this.config.audience
      });
      
      return true;
    } catch (error) {
      return false;
    }
  }

  async decodeToken(token: string): Promise<TokenPayload | null> {
    try {
      const publicKey = await this.keyProvider.getPublicKey();
      
      const decoded = jwt.verify(token, publicKey, {
        algorithms: [this.config.algorithm],
        issuer: this.config.issuer,
        audience: this.config.audience
      }) as any;

      return {
        sub: decoded.sub,
        email: decoded.email,
        scope: decoded.scope || [],
        iss: decoded.iss,
        aud: decoded.aud,
        exp: decoded.exp,
        iat: decoded.iat,
        nbf: decoded.nbf
      };
    } catch (error) {
      return null;
    }
  }
}
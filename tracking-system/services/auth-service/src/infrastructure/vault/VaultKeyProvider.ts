import vault from 'node-vault';
import { KeyProvider, JWKS, JWKSKey } from '../../domain/repositories/KeyProvider';
import crypto from 'crypto';

export interface VaultConfig {
  endpoint: string;
  token: string;
  secretPath: string;
}

export class VaultKeyProvider implements KeyProvider {
  private client: any;
  private cachedKeys: { private?: string; public?: string; keyId?: string } = {};

  constructor(private config: VaultConfig) {
    this.client = vault({
      apiVersion: 'v1',
      endpoint: config.endpoint,
      token: config.token
    });
  }

  async getPrivateKey(): Promise<string> {
    if (this.cachedKeys.private) {
      return this.cachedKeys.private;
    }

    try {
      const result = await this.client.read(this.config.secretPath);
      this.cachedKeys.private = result.data.private_key;
      return this.cachedKeys.private;
    } catch (error) {
      console.error('Failed to get private key from Vault:', error);
      throw new Error('Unable to retrieve private key from Vault');
    }
  }

  async getPublicKey(): Promise<string> {
    if (this.cachedKeys.public) {
      return this.cachedKeys.public;
    }

    try {
      const result = await this.client.read(this.config.secretPath);
      this.cachedKeys.public = result.data.public_key;
      return this.cachedKeys.public;
    } catch (error) {
      console.error('Failed to get public key from Vault:', error);
      throw new Error('Unable to retrieve public key from Vault');
    }
  }

  async getKeyId(): Promise<string> {
    if (this.cachedKeys.keyId) {
      return this.cachedKeys.keyId;
    }

    const publicKey = await this.getPublicKey();
    this.cachedKeys.keyId = crypto
      .createHash('sha256')
      .update(publicKey)
      .digest('hex')
      .substring(0, 8);
    
    return this.cachedKeys.keyId;
  }

  async getJWKS(): Promise<JWKS> {
    const publicKey = await this.getPublicKey();
    const keyId = await this.getKeyId();

    // Convert PEM to JWK format
    const key = crypto.createPublicKey(publicKey);
    const jwk = key.export({ format: 'jwk' }) as any;

    const jwksKey: JWKSKey = {
      kty: 'RSA',
      use: 'sig',
      kid: keyId,
      n: jwk.n,
      e: jwk.e,
      alg: 'RS256'
    };

    return {
      keys: [jwksKey]
    };
  }
}
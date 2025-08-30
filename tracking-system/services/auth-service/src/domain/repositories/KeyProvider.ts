export interface KeyProvider {
  getPrivateKey(): Promise<string>;
  getPublicKey(): Promise<string>;
  getKeyId(): Promise<string>;
}

export interface JWKSKey {
  kty: string;
  use: string;
  kid: string;
  n: string;
  e: string;
  alg: string;
}

export interface JWKS {
  keys: JWKSKey[];
}
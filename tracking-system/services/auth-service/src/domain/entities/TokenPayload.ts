export interface TokenPayload {
  sub: string;           // User ID
  email: string;
  scope: string[];       // Permissions
  iss?: string;          // Issuer
  aud?: string;          // Audience
  exp?: number;          // Expiration
  iat?: number;          // Issued at
  nbf?: number;          // Not before
}

export interface AuthResult {
  token: string;
  user: {
    id: string;
    email: string;
    permissions: string[];
  };
  expiresIn: number;
}
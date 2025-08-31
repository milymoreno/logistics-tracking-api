export const config = {
  port: parseInt(process.env.PORT || '3001'),
  host: process.env.HOST || '0.0.0.0',
  
  database: {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT || '5432'),
    database: process.env.DB_NAME || 'tracking_db',
    user: process.env.DB_USER || 'tracking_user',
    password: process.env.DB_PASSWORD || 'nueva_contraseña_segura',
  },
  
  jwt: {
    secret: process.env.JWT_SECRET || 'your-secret-key',
    expiresIn: process.env.JWT_EXPIRES_IN || '24h',
  },
  
  vault: {
    endpoint: process.env.VAULT_ENDPOINT || 'http://localhost:8200',
    token: process.env.VAULT_TOKEN || 'dev-token',
  },
};
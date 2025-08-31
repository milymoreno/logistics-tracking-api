import Fastify from 'fastify';
import swagger from '@fastify/swagger';
import swaggerUi from '@fastify/swagger-ui';
import { authRoutes } from './routes/auth';

export async function buildApp() {
  const app = Fastify({
    logger: true
  });

  // Register Swagger
  await app.register(swagger, {
    swagger: {
      info: {
        title: 'Auth Service API',
        description: 'Authentication service for tracking system',
        version: '1.0.0'
      },
      host: 'localhost:3001',
      schemes: ['http'],
      consumes: ['application/json'],
      produces: ['application/json'],
    }
  });

  await app.register(swaggerUi, {
    routePrefix: '/docs',
    uiConfig: {
      docExpansion: 'full',
      deepLinking: false
    }
  });

  // Register routes
  await app.register(authRoutes, { prefix: '/api/auth' });

  // Health check
  app.get('/health', async () => {
    return { status: 'ok', service: 'auth-service' };
  });

  return app;
}
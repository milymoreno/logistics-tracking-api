import { FastifyInstance } from 'fastify';
import jwt, { SignOptions } from 'jsonwebtoken';
import { config } from '../config';

export async function authRoutes(app: FastifyInstance) {
  app.post('/login', {
    schema: {
      summary: 'Login de usuario',
      description: 'Autentica un usuario y devuelve un JWT',
      tags: ['auth'],
      body: {
        type: 'object',
        required: ['username', 'password'],
        properties: {
          username: { type: 'string' },
          password: { type: 'string' }
        }
      },
      response: {
        200: {
          type: 'object',
          properties: {
            token: { type: 'string', description: 'JWT emitido' }
          }
        },
        401: {
          type: 'object',
          properties: {
            message: { type: 'string', description: 'Credenciales inválidas' }
          }
        }
      }
    }
  }, async (request, reply) => {
    const { username, password } = request.body as { username: string, password: string };
    if (username === 'admin' && password === 'admin') {
      // Firma real del JWT
      const signOptions: SignOptions = {
        expiresIn: config.jwt.expiresIn ? parseInt(config.jwt.expiresIn) : 86400 // 86400 segundos = 24 horas
       };
      
      const token = jwt.sign(
        { username },
        String(config.jwt.secret),
        signOptions
      );
      return { token };
    }
    reply.code(401).send({ message: 'Credenciales inválidas' });
  });
}
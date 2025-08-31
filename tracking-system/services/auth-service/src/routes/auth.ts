import { FastifyInstance } from 'fastify';
import jwt from 'jsonwebtoken';
import { config } from '../config';
import { createDatabaseConnection } from '../infrastructure/database/connection';
import bcrypt from 'bcrypt';
import { PostgresUserRepository } from '../infrastructure/database/PostgresUserRepository';

const pool = createDatabaseConnection();
const userRepository = new PostgresUserRepository(pool);

export async function authRoutes(app: FastifyInstance) {
  // Login
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
        200: { type: 'object', properties: { token: { type: 'string' } } },
        401: { type: 'object', properties: { message: { type: 'string' } } }
      }
    }
  }, async (request, reply) => {
    const { username, password } = request.body as { username: string, password: string };
    const user = await userRepository.findByEmail(username);
    if (!user) return reply.code(401).send({ message: 'Credenciales inválidas' });
    const valid = await bcrypt.compare(password, user.hashedPassword);
    if (!valid) return reply.code(401).send({ message: 'Credenciales inválidas' });

    const signOptions: jwt.SignOptions = {
         expiresIn: config.jwt.expiresIn ? parseInt(config.jwt.expiresIn) : 86400
    };
    const token = jwt.sign(
      { userId: user.id, email: user.email },
      config.jwt.secret,
      signOptions
    );
    return { token };
  });

  // Crear usuario
  app.post('/users', {
    schema: {
      summary: 'Crear usuario',
      tags: ['users'],
      body: {
        type: 'object',
        required: ['email', 'password'],
        properties: {
          email: { type: 'string' },
          password: { type: 'string' },
          permissions: { type: 'array', items: { type: 'string' } }
        }
      },
      response: {
        201: { type: 'object', properties: { id: { type: 'string' }, email: { type: 'string' } } },
        400: { type: 'object', properties: { message: { type: 'string' } } }
      }
    }
  }, async (request, reply) => {
    const { email, password, permissions } = request.body as { email: string, password: string, permissions?: string[] };
    const exists = await userRepository.findByEmail(email);
    if (exists) return reply.code(400).send({ message: 'Usuario ya existe' });
    // You may want to extract these from the request body or set defaults
    const user = await userRepository.create({
      email,
      password,
      permissions
    });
    // If you need to handle permissions, do it after user creation or update the type accordingly
    return reply.code(201).send({ id: user.id, email: user.email });
  });

  // Obtener usuario por ID
  app.get('/users/:id', {
    schema: {
      summary: 'Obtener usuario por ID',
      tags: ['users'],
      params: { type: 'object', properties: { id: { type: 'string' } }, required: ['id'] },
      response: {
        200: { type: 'object' },
        404: { type: 'object', properties: { message: { type: 'string' } } }
      }
    }
  }, async (request, reply) => {
    const { id } = request.params as { id: string };
    const user = await userRepository.findById(id);
    if (!user) return reply.code(404).send({ message: 'Usuario no encontrado' });
    return user;
  });

  // Obtener usuario por email (opcional)
  app.get('/users/email/:email', {
    schema: {
      summary: 'Obtener usuario por email',
      tags: ['users'],
      params: { type: 'object', properties: { email: { type: 'string' } }, required: ['email'] },
      response: {
        200: { type: 'object' },
        404: { type: 'object', properties: { message: { type: 'string' } } }
      }
    }
  }, async (request, reply) => {
    const { email } = request.params as { email: string };
    const user = await userRepository.findByEmail(email);
    if (!user) return reply.code(404).send({ message: 'Usuario no encontrado' });
    return user;
  });

  // Actualizar usuario
  app.put('/users/:id', {
    schema: {
      summary: 'Actualizar usuario',
      tags: ['users'],
      params: { type: 'object', properties: { id: { type: 'string' } }, required: ['id'] },
      body: {
        type: 'object',
        properties: {
          email: { type: 'string' },
          password: { type: 'string' },
          permissions: { type: 'array', items: { type: 'string' } },
          isActive: { type: 'boolean' }
        }
      },
      response: {
        200: { type: 'object' },
        404: { type: 'object', properties: { message: { type: 'string' } } }
      }
    }
  }, async (request, reply) => {
    const { id } = request.params as { id: string };
    const { email, password, permissions, isActive } = request.body as any;
    const userData: any = {};
    if (email) userData.email = email;
    if (permissions) userData.permissions = permissions;
    if (isActive !== undefined) userData.isActive = isActive;
    if (password) userData.hashedPassword = await bcrypt.hash(password, 10);
    const user = await userRepository.update(id, userData);
    if (!user) return reply.code(404).send({ message: 'Usuario no encontrado' });
    return user;
  });

  // Eliminar usuario
  app.delete('/users/:id', {
    schema: {
      summary: 'Eliminar usuario',
      tags: ['users'],
      params: { type: 'object', properties: { id: { type: 'string' } }, required: ['id'] },
      response: {
        204: { type: 'null' }, // o simplemente {}
        404: { type: 'object', properties: { message: { type: 'string' } } }
      }
    }
  }, async (request, reply) => {
    const { id } = request.params as { id: string };
    const deleted = await userRepository.delete(id);
    if (!deleted) return reply.code(404).send({ message: 'Usuario no encontrado' });
    return reply.code(204).send();
  });
}
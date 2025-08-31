import { Pool } from 'pg';
import { UserRepository } from '../../domain/repositories/UserRepository';
import { User, CreateUserRequest } from '../../domain/entities/User';
import { v4 as uuid } from 'uuid';
import bcrypt from 'bcrypt';

export class PostgresUserRepository implements UserRepository {
  constructor(private readonly pool: Pool) {}

  async delete(id: string): Promise<boolean> {
    const query = 'DELETE FROM users WHERE id = $1';
    const result = await this.pool.query(query, [id]);
    return (typeof result.rowCount === 'number' ? result.rowCount : 0) > 0;
  }

  async findById(id: string): Promise<User | null> {
    const query = 'SELECT * FROM users WHERE id = $1 AND is_active = true';
    console.log('findById QUERY:', query, 'VALUES:', [id]); 
    const result = await this.pool.query(query, [id]);
    if (result.rows.length === 0) {
      return null;
    }
    return this.mapRowToUser(result.rows[0]);
  }

  async findByEmail(email: string): Promise<User | null> {
    const query = 'SELECT * FROM users WHERE email = $1 AND is_active = true';
    const result = await this.pool.query(query, [email]);
    if (result.rows.length === 0) {
      return null;
    }
    return this.mapRowToUser(result.rows[0]);
  }

  async create(userData: CreateUserRequest): Promise<User> {
    const id = uuid();
    const hashedPassword = await bcrypt.hash(userData.password, 10);
    const now = new Date();

    const query = `
      INSERT INTO users (id, email, hashed_password, permissions, is_active, created_at)
      VALUES ($1, $2, $3, $4, $5, $6)
      RETURNING *
    `;

    const values = [
      id,
      userData.email,
      hashedPassword,
      userData.permissions ?? [], // <-- SOLO el array, no JSON.stringify
      true,
      now
    ];

    const result = await this.pool.query(query, values);
    return this.mapRowToUser(result.rows[0]);
  }

  async update(id: string, userData: Partial<User>): Promise<User | null> {
    const fields = [];
    const values = [];
    let paramCount = 1;

    if (userData.email) {
      fields.push(`email = $${paramCount++}`);
      values.push(userData.email);
    }
    if ((userData as any).hashedPassword) {
      fields.push(`hashed_password = $${paramCount++}`);
      values.push((userData as any).hashedPassword);
    }
    if ('permissions' in userData && userData.permissions !== undefined) {
      fields.push(`permissions = $${paramCount++}`);
      values.push(userData.permissions); // <-- SOLO el array
    }
    if (userData.isActive !== undefined) {
      fields.push(`is_active = $${paramCount++}`);
      values.push(userData.isActive);
    }
    if ('lastLogin' in userData && userData.lastLogin !== undefined) {
      fields.push(`last_login = $${paramCount++}`);
      values.push(userData.lastLogin);
    }

    if (fields.length === 0) {
      return this.findById(id);
    }

    values.push(id);

    const query = `
      UPDATE users 
      SET ${fields.join(', ')}
      WHERE id = $${paramCount}
      RETURNING *
    `;

    const result = await this.pool.query(query, values);

    if (result.rows.length === 0) {
      return null;
    }

    return this.mapRowToUser(result.rows[0]);
  }

  private mapRowToUser(row: any): User {
    return {
      id: row.id,
      email: row.email,
      hashedPassword: row.hashed_password,
      permissions: row.permissions,
      isActive: row.is_active,
      lastLogin: row.last_login,
      createdAt: row.created_at
    };
  }
}
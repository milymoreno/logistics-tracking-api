import { Pool } from 'pg';
import { UserRepository } from '../../domain/repositories/UserRepository';
import { User, CreateUserRequest, UserRole } from '../../domain/entities/User';
import { v4 as uuidv4 } from 'uuid';
import bcrypt from 'bcrypt';

export class PostgresUserRepository implements UserRepository {
  constructor(private pool: Pool) {}

  async findById(id: string): Promise<User | null> {
    const query = 'SELECT * FROM users WHERE id = $1';
    const result = await this.pool.query(query, [id]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return this.mapRowToUser(result.rows[0]);
  }

  async findByEmail(email: string): Promise<User | null> {
    const query = 'SELECT * FROM users WHERE email = $1';
    const result = await this.pool.query(query, [email]);
    
    if (result.rows.length === 0) {
      return null;
    }
    
    return this.mapRowToUser(result.rows[0]);
  }

  async create(userData: CreateUserRequest): Promise<User> {
    const id = uuidv4();
    const hashedPassword = await bcrypt.hash(userData.password, 10);
    const now = new Date();
    
    const query = `
      INSERT INTO users (id, email, password, first_name, last_name, role, is_active, created_at, updated_at)
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
      RETURNING *
    `;
    
    const values = [
      id,
      userData.email,
      hashedPassword,
      userData.firstName,
      userData.lastName,
      userData.role,
      true,
      now,
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
    
    if (userData.firstName) {
      fields.push(`first_name = $${paramCount++}`);
      values.push(userData.firstName);
    }
    
    if (userData.lastName) {
      fields.push(`last_name = $${paramCount++}`);
      values.push(userData.lastName);
    }
    
    if (userData.role) {
      fields.push(`role = $${paramCount++}`);
      values.push(userData.role);
    }
    
    if (userData.isActive !== undefined) {
      fields.push(`is_active = $${paramCount++}`);
      values.push(userData.isActive);
    }

    fields.push(`updated_at = $${paramCount++}`);
    values.push(new Date());
    
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

  async delete(id: string): Promise<boolean> {
    const query = 'DELETE FROM users WHERE id = $1';
    const result = await this.pool.query(query, [id]);
    return result.rowCount > 0;
  }

  private mapRowToUser(row: any): User {
    return {
      id: row.id,
      email: row.email,
      password: row.password,
      firstName: row.first_name,
      lastName: row.last_name,
      role: row.role as UserRole,
      isActive: row.is_active,
      createdAt: row.created_at,
      updatedAt: row.updated_at
    };
  }
}
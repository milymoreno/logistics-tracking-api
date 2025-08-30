export interface User {
  id: string;
  email: string;
  hashedPassword: string;
  permissions: string[];
  createdAt: Date;
  isActive: boolean;
}

export interface PublicUser {
  id: string;
  email: string;
  permissions: string[];
}

export class UserEntity implements User {
  constructor(
    public readonly id: string,
    public readonly email: string,
    public readonly hashedPassword: string,
    public readonly permissions: string[],
    public readonly createdAt: Date,
    public readonly isActive: boolean
  ) {}

  toPublic(): PublicUser {
    return {
      id: this.id,
      email: this.email,
      permissions: this.permissions
    };
  }

  hasPermission(permission: string): boolean {
    return this.permissions.includes(permission);
  }
}
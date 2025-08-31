---
title: Logistics Tracking API
author: Mily Moreno (Mildred María Moreno Liscano )
date: 30-08-2025
---

# 📄 Configuración y Esquema de Base de Datos para Auth Service

## 1. Configuración de conexión (`src/config/index.ts`)

```typescript
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
```

---

## 2. Esquema de la tabla `users`

```sql
CREATE TABLE public.users (
  id uuid DEFAULT uuid_generate_v4() NOT NULL,
  email varchar(255) NOT NULL,
  hashed_password varchar(255) NOT NULL,
  permissions _text DEFAULT '{}'::text[] NULL,
  is_active bool DEFAULT true NULL,
  last_login timestamp NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
  CONSTRAINT users_email_key UNIQUE (email),
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_users_email ON public.users USING btree (email);
```

---

## 3. Permisos recomendados para el usuario de la app

```sql
GRANT USAGE ON SCHEMA public TO tracking_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tracking_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tracking_user;
```

---

## 4. Insertar un usuario de prueba

1. Genera el hash de la contraseña en Node.js:
   ```js
   const bcrypt = require('bcrypt');
   bcrypt.hash('tu_clave', 10).then(console.log);
   ```

2. Inserta el usuario:
   ```sql
   INSERT INTO public.users (email, hashed_password, permissions, is_active)
   VALUES (
     'admin@tracking.com',
     '$2b$10$HASH_GENERADO_AQUI',
     ARRAY['tracking:read', 'tracking:write', 'admin:all'],
     true
   );
   ```

---

## 5. Consultas útiles

- **Ver todos los usuarios:**
  ```sql
  SELECT * FROM public.users;
  ```

- **Actualizar contraseña:**
  ```sql
  UPDATE public.users
  SET hashed_password = '$2b$10$HASH_NUEVO'
  WHERE email = 'admin@tracking.com';
  ```

- **Verificar usuario activo:**
  ```sql
  SELECT * FROM public.users WHERE email = 'admin@tracking.com' AND is_active = true;
  ```

---

## 6. Notas

- El campo `permissions` es un arreglo de texto para roles/permisos.
- El campo `is_active` permite deshabilitar usuarios sin borrarlos.
- Usa siempre el schema `public` en tus queries para evitar errores de relación.

---
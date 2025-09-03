#!/bin/bash

# Script para configurar Vault con claves JWT para el sistema de tracking

echo "🔐 Configurando Vault para JWT..."

# Variables
VAULT_ADDR="http://localhost:8200"
VAULT_TOKEN="myroot"

# Esperar a que Vault esté disponible
echo "⏳ Esperando a que Vault esté disponible..."
until curl -s $VAULT_ADDR/v1/sys/health > /dev/null; do
    echo "Esperando Vault..."
    sleep 2
done

echo "✅ Vault está disponible"

# Configurar variables de entorno
export VAULT_ADDR=$VAULT_ADDR
export VAULT_TOKEN=$VAULT_TOKEN

# Generar par de claves RSA para JWT
echo "🔑 Generando par de claves RSA..."
openssl genrsa -out private_key.pem 2048
openssl rsa -in private_key.pem -pubout -out public_key.pem

# Leer las claves
PRIVATE_KEY=$(cat private_key.pem)
PUBLIC_KEY=$(cat public_key.pem)

# Habilitar el motor de secretos KV v2
echo "📝 Habilitando motor de secretos KV..."
vault secrets enable -path=secret kv-v2

# Almacenar las claves en Vault
echo "💾 Almacenando claves JWT en Vault..."
vault kv put secret/tracking/jwt \
    private_key="$PRIVATE_KEY" \
    public_key="$PUBLIC_KEY" \
    algorithm="RS256" \
    issuer="bt-core-system" \
    audience="tracking-service"

# Crear política para el servicio de tracking
echo "📋 Creando política de acceso..."
vault policy write tracking-service - <<EOF
path "secret/data/tracking/jwt" {
  capabilities = ["read"]
}
path "auth/jwt/keys" {
  capabilities = ["read"]
}
EOF

# Habilitar autenticación JWT
echo "🔐 Configurando autenticación JWT..."
vault auth enable jwt

# Configurar JWT auth method
vault write auth/jwt/config \
    bound_issuer="bt-core-system" \
    oidc_discovery_url="http://localhost:8080"

# Crear rol para el servicio
vault write auth/jwt/role/tracking-service \
    bound_audiences="tracking-service" \
    bound_subject="tracking-service" \
    user_claim="sub" \
    role_type="jwt" \
    policies="tracking-service" \
    ttl=1h

# Crear un alias para las claves JWT en el path esperado
echo "🔗 Creando alias para claves JWT..."
vault kv put secret/auth/jwt/keys \
    public_key="$PUBLIC_KEY"

# Generar un token de ejemplo para pruebas
echo "🎫 Generando token JWT de ejemplo..."

# Crear payload JWT
JWT_HEADER='{"alg":"RS256","typ":"JWT"}'
JWT_PAYLOAD='{
  "sub": "test-user-123",
  "iss": "bt-core-system",
  "aud": "tracking-service",
  "exp": '$(date -d "+1 hour" +%s)',
  "iat": '$(date +%s)',
  "role": "user",
  "name": "Test User"
}'

# Codificar en base64
JWT_HEADER_B64=$(echo -n "$JWT_HEADER" | base64 -w 0 | tr -d '=' | tr '/+' '_-')
JWT_PAYLOAD_B64=$(echo -n "$JWT_PAYLOAD" | base64 -w 0 | tr -d '=' | tr '/+' '_-')

# Crear firma
JWT_UNSIGNED="$JWT_HEADER_B64.$JWT_PAYLOAD_B64"
JWT_SIGNATURE=$(echo -n "$JWT_UNSIGNED" | openssl dgst -sha256 -sign private_key.pem | base64 -w 0 | tr -d '=' | tr '/+' '_-')

# Token completo
JWT_TOKEN="$JWT_UNSIGNED.$JWT_SIGNATURE"

echo ""
echo "✅ Configuración de Vault completada!"
echo ""
echo "📋 Información importante:"
echo "  - Vault URL: $VAULT_ADDR"
echo "  - Root Token: $VAULT_TOKEN"
echo "  - Política creada: tracking-service"
echo ""
echo "🎫 Token JWT de ejemplo para pruebas:"
echo "Bearer $JWT_TOKEN"
echo ""
echo "🧪 Comando de prueba:"
echo "curl -H 'Authorization: Bearer $JWT_TOKEN' http://localhost:8080/api/v1/auth/test"
echo ""

# Limpiar archivos temporales
rm -f private_key.pem public_key.pem

echo "🎉 ¡Configuración completada!"
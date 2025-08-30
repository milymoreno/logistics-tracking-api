#!/bin/bash

# Wait for Vault to be ready
sleep 5

# Generate RSA key pair for JWT signing
openssl genrsa -out /tmp/private_key.pem 2048
openssl rsa -in /tmp/private_key.pem -pubout -out /tmp/public_key.pem

# Store keys in Vault
vault kv put secret/jwt-keys \
  private_key="$(cat /tmp/private_key.pem)" \
  public_key="$(cat /tmp/public_key.pem)"

echo "JWT keys stored in Vault successfully"

# Clean up temporary files
rm /tmp/private_key.pem /tmp/public_key.pem
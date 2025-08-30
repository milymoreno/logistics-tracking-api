#!/bin/bash

echo "🚀 Setting up Tracking System - Microservices"
echo "=============================================="

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo "❌ Docker is required but not installed"
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo "❌ Node.js is required but not installed"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "❌ Java is required but not installed"
    exit 1
fi

echo "✅ All prerequisites are installed"

# Setup Auth Service
echo "🔧 Setting up Auth Service (Node.js)..."
cd services/auth-service
if [ ! -d "node_modules" ]; then
    npm install
fi
cd ../..

# Setup Core Service (will be created later)
echo "🔧 Core Service setup will be done with Quarkus CLI"

# Setup infrastructure
echo "🐳 Setting up infrastructure..."
cd infrastructure

# Make scripts executable
chmod +x vault/init.sh

echo "✅ Setup completed!"
echo ""
echo "Next steps:"
echo "1. Start infrastructure: cd infrastructure && docker-compose up -d vault postgres redis"
echo "2. Initialize Vault keys: docker exec tracking-vault /vault/init.sh"
echo "3. Start auth-service: cd services/auth-service && npm run dev"
echo "4. Create core-service with Quarkus CLI"
echo ""
echo "🎯 Ready to start development!"
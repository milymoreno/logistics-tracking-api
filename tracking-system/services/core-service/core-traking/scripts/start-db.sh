#!/bin/bash

# =====================================================
# Script para iniciar la base de datos PostgreSQL
# =====================================================

echo "🚀 Iniciando base de datos PostgreSQL para Core Tracking Service..."

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo. Por favor inicia Docker Desktop."
    exit 1
fi

# Detener contenedores existentes si están corriendo
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Limpiar volúmenes si se pasa el parámetro --clean
if [ "$1" = "--clean" ]; then
    echo "🧹 Limpiando volúmenes de datos..."
    docker-compose down -v
    docker volume prune -f
fi

# Iniciar los servicios
echo "🐘 Iniciando PostgreSQL..."
docker-compose up -d postgres

# Esperar a que PostgreSQL esté listo
echo "⏳ Esperando a que PostgreSQL esté listo..."
timeout=60
counter=0

while ! docker-compose exec postgres pg_isready -U tracking_user -d tracking_db > /dev/null 2>&1; do
    if [ $counter -ge $timeout ]; then
        echo "❌ Timeout: PostgreSQL no se inició en $timeout segundos"
        docker-compose logs postgres
        exit 1
    fi
    
    echo "   Esperando... ($counter/$timeout)"
    sleep 2
    counter=$((counter + 2))
done

echo "✅ PostgreSQL está listo!"

# Mostrar información de conexión
echo ""
echo "📊 Información de conexión:"
echo "   Host: localhost"
echo "   Puerto: 5432"
echo "   Base de datos: tracking_db"
echo "   Usuario: tracking_user"
echo "   Contraseña: tracking_pass"
echo ""

# Verificar que las tablas se crearon correctamente
echo "🔍 Verificando estructura de la base de datos..."
docker-compose exec postgres psql -U tracking_user -d tracking_db -c "\dt"

echo ""
echo "📈 Estadísticas de datos de prueba:"
docker-compose exec postgres psql -U tracking_user -d tracking_db -c "
SELECT 
    COUNT(*) as total_eventos,
    COUNT(DISTINCT tracking_id) as tracking_ids_unicos,
    COUNT(DISTINCT user_id) as usuarios_unicos
FROM tracking_events;
"

echo ""
echo "🎯 Ejemplos de consultas:"
echo "   Ver todos los eventos: docker-compose exec postgres psql -U tracking_user -d tracking_db -c 'SELECT * FROM tracking_events ORDER BY created_at DESC;'"
echo "   Ver por usuario: docker-compose exec postgres psql -U tracking_user -d tracking_db -c \"SELECT * FROM tracking_events WHERE user_id = 'user123';\""
echo ""

# Preguntar si quiere iniciar Adminer también
read -p "¿Quieres iniciar Adminer (interfaz web para administrar la BD)? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🌐 Iniciando Adminer..."
    docker-compose up -d adminer
    echo "✅ Adminer disponible en: http://localhost:8081"
    echo "   Sistema: PostgreSQL"
    echo "   Servidor: postgres"
    echo "   Usuario: tracking_user"
    echo "   Contraseña: tracking_pass"
    echo "   Base de datos: tracking_db"
fi

echo ""
echo "🎉 ¡Base de datos lista para usar!"
echo "💡 Para detener: docker-compose down"
echo "💡 Para limpiar datos: ./scripts/start-db.sh --clean"
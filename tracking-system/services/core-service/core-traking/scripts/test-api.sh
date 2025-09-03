#!/bin/bash

# =====================================================
# Script para probar la API del Core Tracking Service
# =====================================================

BASE_URL="http://localhost:8080/api/v1"

echo "🧪 Probando Core Tracking Service API..."
echo "📍 Base URL: $BASE_URL"
echo ""

# Función para hacer requests con curl
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo "🔍 $description"
    echo "   $method $endpoint"
    
    if [ -n "$data" ]; then
        echo "   Data: $data"
        response=$(curl -s -X $method "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data" \
            -w "\n%{http_code}")
    else
        response=$(curl -s -X $method "$BASE_URL$endpoint" \
            -w "\n%{http_code}")
    fi
    
    # Separar respuesta y código HTTP
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo "   ✅ Success ($http_code)"
        echo "$body" | jq . 2>/dev/null || echo "$body"
    else
        echo "   ❌ Error ($http_code)"
        echo "$body"
    fi
    
    echo ""
    sleep 1
}

# Verificar que jq esté instalado (para formatear JSON)
if ! command -v jq &> /dev/null; then
    echo "💡 Tip: Instala 'jq' para mejor formato de JSON: sudo apt install jq"
    echo ""
fi

# 1. Health Check
make_request "GET" "/health" "" "Health Check Básico"

# 2. Health Check Detallado
make_request "GET" "/health/detailed" "" "Health Check Detallado"

# 3. Obtener todos los trackings (datos de prueba)
make_request "GET" "/tracking" "" "Obtener Todos los Trackings"

# 4. Obtener trackings por usuario
make_request "GET" "/tracking/user/user123" "" "Obtener Trackings del Usuario 'user123'"

# 5. Obtener historial de un tracking específico
make_request "GET" "/tracking/TRK-TEST001/history" "" "Obtener Historial del Tracking 'TRK-TEST001'"

# 6. Obtener trackings por status
make_request "GET" "/tracking/status/COMPLETED" "" "Obtener Trackings con Status 'COMPLETED'"

# 7. Crear un nuevo tracking
new_tracking_data='{
    "userId": "testuser",
    "status": "CREATED",
    "description": "Nuevo tracking creado desde script de prueba",
    "metadata": "{\"source\": \"test-script\", \"timestamp\": \"'$(date -Iseconds)'\"}"
}'

make_request "POST" "/tracking" "$new_tracking_data" "Crear Nuevo Tracking"

# 8. Obtener el tracking recién creado (asumiendo que se creó con éxito)
make_request "GET" "/tracking/user/testuser" "" "Verificar Tracking Creado para 'testuser'"

echo "🎉 Pruebas completadas!"
echo ""
echo "📊 Endpoints adicionales disponibles:"
echo "   GET  /api/v1/tracking/id/{id}           - Obtener por ID interno"
echo "   PUT  /api/v1/tracking/{trackingId}/status - Actualizar status"
echo ""
echo "🌐 Documentación completa en: http://localhost:8080/swagger-ui"
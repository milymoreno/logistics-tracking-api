@echo off
REM =====================================================
REM Script para probar la API del Core Tracking Service
REM =====================================================

set BASE_URL=http://localhost:8080/api/v1

echo 🧪 Probando Core Tracking Service API...
echo 📍 Base URL: %BASE_URL%
echo.

REM 1. Health Check
echo 🔍 Health Check Básico
echo    GET /health
curl -s "%BASE_URL%/health"
echo.
echo.

REM 2. Health Check Detallado
echo 🔍 Health Check Detallado
echo    GET /health/detailed
curl -s "%BASE_URL%/health/detailed"
echo.
echo.

REM 3. Obtener todos los trackings
echo 🔍 Obtener Todos los Trackings
echo    GET /tracking
curl -s "%BASE_URL%/tracking"
echo.
echo.

REM 4. Obtener trackings por usuario
echo 🔍 Obtener Trackings del Usuario 'user123'
echo    GET /tracking/user/user123
curl -s "%BASE_URL%/tracking/user/user123"
echo.
echo.

REM 5. Obtener historial de tracking
echo 🔍 Obtener Historial del Tracking 'TRK-TEST001'
echo    GET /tracking/TRK-TEST001/history
curl -s "%BASE_URL%/tracking/TRK-TEST001/history"
echo.
echo.

REM 6. Obtener trackings por status
echo 🔍 Obtener Trackings con Status 'COMPLETED'
echo    GET /tracking/status/COMPLETED
curl -s "%BASE_URL%/tracking/status/COMPLETED"
echo.
echo.

REM 7. Crear nuevo tracking
echo 🔍 Crear Nuevo Tracking
echo    POST /tracking
curl -s -X POST "%BASE_URL%/tracking" ^
    -H "Content-Type: application/json" ^
    -d "{\"userId\": \"testuser\", \"status\": \"CREATED\", \"description\": \"Nuevo tracking desde script Windows\", \"metadata\": \"{\\\"source\\\": \\\"test-script-windows\\\"}\"}"
echo.
echo.

REM 8. Verificar tracking creado
echo 🔍 Verificar Tracking Creado para 'testuser'
echo    GET /tracking/user/testuser
curl -s "%BASE_URL%/tracking/user/testuser"
echo.
echo.

echo 🎉 Pruebas completadas!
echo.
echo 📊 Endpoints adicionales disponibles:
echo    GET  /api/v1/tracking/id/{id}           - Obtener por ID interno
echo    PUT  /api/v1/tracking/{trackingId}/status - Actualizar status
echo.
echo 🌐 Documentación completa en: http://localhost:8080/swagger-ui
pause
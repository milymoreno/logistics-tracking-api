@echo off
REM =====================================================
REM Script para iniciar la base de datos PostgreSQL en Windows
REM =====================================================

echo 🚀 Iniciando base de datos PostgreSQL para Core Tracking Service...

REM Verificar si Docker está corriendo
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker no está corriendo. Por favor inicia Docker Desktop.
    pause
    exit /b 1
)

REM Detener contenedores existentes si están corriendo
echo 🛑 Deteniendo contenedores existentes...
docker-compose down

REM Limpiar volúmenes si se pasa el parámetro --clean
if "%1"=="--clean" (
    echo 🧹 Limpiando volúmenes de datos...
    docker-compose down -v
    docker volume prune -f
)

REM Iniciar los servicios
echo 🐘 Iniciando PostgreSQL...
docker-compose up -d postgres

REM Esperar a que PostgreSQL esté listo
echo ⏳ Esperando a que PostgreSQL esté listo...
set timeout=60
set counter=0

:wait_loop
docker-compose exec postgres pg_isready -U tracking_user -d tracking_db >nul 2>&1
if errorlevel 0 goto ready

if %counter% geq %timeout% (
    echo ❌ Timeout: PostgreSQL no se inició en %timeout% segundos
    docker-compose logs postgres
    pause
    exit /b 1
)

echo    Esperando... (%counter%/%timeout%)
timeout /t 2 /nobreak >nul
set /a counter+=2
goto wait_loop

:ready
echo ✅ PostgreSQL está listo!

REM Mostrar información de conexión
echo.
echo 📊 Información de conexión:
echo    Host: localhost
echo    Puerto: 5432
echo    Base de datos: tracking_db
echo    Usuario: tracking_user
echo    Contraseña: tracking_pass
echo.

REM Verificar que las tablas se crearon correctamente
echo 🔍 Verificando estructura de la base de datos...
docker-compose exec postgres psql -U tracking_user -d tracking_db -c "\dt"

echo.
echo 📈 Estadísticas de datos de prueba:
docker-compose exec postgres psql -U tracking_user -d tracking_db -c "SELECT COUNT(*) as total_eventos, COUNT(DISTINCT tracking_id) as tracking_ids_unicos, COUNT(DISTINCT user_id) as usuarios_unicos FROM tracking_events;"

echo.
echo 🎯 Ejemplos de consultas:
echo    Ver todos los eventos: docker-compose exec postgres psql -U tracking_user -d tracking_db -c "SELECT * FROM tracking_events ORDER BY created_at DESC;"
echo    Ver por usuario: docker-compose exec postgres psql -U tracking_user -d tracking_db -c "SELECT * FROM tracking_events WHERE user_id = 'user123';"
echo.

REM Preguntar si quiere iniciar Adminer también
set /p adminer="¿Quieres iniciar Adminer (interfaz web para administrar la BD)? (y/n): "
if /i "%adminer%"=="y" (
    echo 🌐 Iniciando Adminer...
    docker-compose up -d adminer
    echo ✅ Adminer disponible en: http://localhost:8081
    echo    Sistema: PostgreSQL
    echo    Servidor: postgres
    echo    Usuario: tracking_user
    echo    Contraseña: tracking_pass
    echo    Base de datos: tracking_db
)

echo.
echo 🎉 ¡Base de datos lista para usar!
echo 💡 Para detener: docker-compose down
echo 💡 Para limpiar datos: scripts\start-db.bat --clean
pause
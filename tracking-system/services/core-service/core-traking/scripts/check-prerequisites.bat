@echo off
REM =====================================================
REM Script para verificar prerrequisitos del sistema
REM =====================================================

echo 🔍 Verificando prerrequisitos para Core Tracking Service...
echo.

set all_ok=true

REM Verificar Java
echo 📋 Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java NO está instalado o no está en el PATH
    echo 💡 Sigue las instrucciones en SETUP-JAVA.md
    set all_ok=false
) else (
    echo ✅ Java está instalado
    java -version 2>&1 | findstr /C:"version" | head -1
)
echo.

REM Verificar JAVA_HOME
echo 📋 Verificando JAVA_HOME...
if "%JAVA_HOME%"=="" (
    echo ❌ JAVA_HOME no está configurado
    echo 💡 Configura JAVA_HOME en las variables de entorno
    set all_ok=false
) else (
    echo ✅ JAVA_HOME está configurado: %JAVA_HOME%
)
echo.

REM Verificar Docker
echo 📋 Verificando Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker NO está instalado
    echo 💡 Instala Docker Desktop desde https://docker.com/products/docker-desktop
    set all_ok=false
) else (
    echo ✅ Docker está instalado
    docker --version
)
echo.

REM Verificar si Docker está corriendo
echo 📋 Verificando si Docker está corriendo...
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker NO está corriendo
    echo 💡 Inicia Docker Desktop manualmente
    set all_ok=false
) else (
    echo ✅ Docker está corriendo
)
echo.

REM Verificar Maven wrapper
echo 📋 Verificando Maven wrapper...
if exist "mvnw.cmd" (
    echo ✅ Maven wrapper encontrado
    mvnw.cmd --version >nul 2>&1
    if errorlevel 1 (
        echo ❌ Maven wrapper no funciona correctamente
        echo 💡 Verifica que Java esté instalado y JAVA_HOME configurado
        set all_ok=false
    ) else (
        echo ✅ Maven wrapper funciona correctamente
    )
) else (
    echo ❌ Maven wrapper NO encontrado
    echo 💡 El archivo mvnw.cmd debería estar en la raíz del proyecto
    set all_ok=false
)
echo.

REM Verificar curl (para testing)
echo 📋 Verificando curl (para testing)...
curl --version >nul 2>&1
if errorlevel 1 (
    echo ⚠️  curl no está disponible
    echo 💡 curl es opcional, pero útil para probar la API
    echo 💡 Está incluido en Windows 10+ por defecto
) else (
    echo ✅ curl está disponible
)
echo.

REM Resumen final
echo ================================================
if "%all_ok%"=="true" (
    echo 🎉 ¡Todos los prerrequisitos están listos!
    echo.
    echo 🚀 Próximos pasos:
    echo    1. Ejecutar: scripts\start-db.bat
    echo    2. Ejecutar: mvnw.cmd quarkus:dev
    echo    3. Abrir: http://localhost:8080/swagger-ui
    echo.
) else (
    echo ❌ Algunos prerrequisitos faltan
    echo.
    echo 📋 Para continuar:
    echo    1. Instalar/configurar los elementos faltantes
    echo    2. Reiniciar PowerShell/CMD
    echo    3. Ejecutar este script nuevamente
    echo.
    echo 📚 Documentación disponible:
    echo    - SETUP-JAVA.md - Para instalar Java
    echo    - SETUP-DATABASE.md - Para configurar PostgreSQL
    echo    - TESTING-GUIDE.md - Guía completa de testing
)
echo ================================================

pause
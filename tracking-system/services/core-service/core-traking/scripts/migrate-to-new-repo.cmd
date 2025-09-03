@echo off
echo 🔄 Migrando a nuevo repositorio "tracking"...
echo.

echo 📝 Paso 1: Guardando cambios actuales...
git add .
git commit -m "feat: complete tracking microservice with JWT security

- Implement clean architecture tracking system  
- Add JWT authentication with Vault integration
- Include comprehensive security tests
- Add complete API documentation
- Setup PostgreSQL and Vault with Docker"

echo 📤 Paso 2: Subiendo al repositorio actual...
git push

echo.
echo 🎯 Paso 3: Instrucciones para crear nuevo repositorio
echo.
echo 1. Ve a GitHub.com
echo 2. Click "New repository"  
echo 3. Nombre: tracking
echo 4. Descripción: "Microservicio de tracking con autenticación JWT y Vault"
echo 5. NO inicializar con README
echo 6. Crear repositorio
echo.
echo 📋 Paso 4: Copia la URL del nuevo repositorio y ejecuta:
echo.
echo git remote set-url origin https://github.com/TU_USUARIO/tracking.git
echo git push -u origin main
echo.
echo ✅ ¡Listo! Tu proyecto estará en el nuevo repositorio "tracking"
echo.

pause
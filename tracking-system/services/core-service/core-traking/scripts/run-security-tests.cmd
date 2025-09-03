@echo off
echo 🔐 Ejecutando tests de seguridad...
echo.

echo 📋 Tests incluidos:
echo   - SecurityTest: Verifica que endpoints requieren autenticacion
echo   - JwtValidationTest: Prueba validacion JWT con mocks
echo   - SecurityIntegrationTest: Tests de integracion
echo   - SecurityDisabledTest: Comportamiento sin seguridad
echo.

echo ⚡ Ejecutando tests...
mvn test -Dtest="com.bt.core.tracking.security.*Test"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Todos los tests de seguridad pasaron correctamente!
    echo.
    echo 📊 Resumen de verificaciones:
    echo   ✓ Endpoints protegidos sin token: RECHAZADOS (401)
    echo   ✓ Tokens con formato invalido: RECHAZADOS (401)
    echo   ✓ Tokens JWT invalidos: RECHAZADOS (401)
    echo   ✓ Endpoints publicos: ACCESIBLES sin token
    echo   ✓ Headers de seguridad: PRESENTES
    echo   ✓ Configuracion de seguridad: FUNCIONAL
    echo.
) else (
    echo.
    echo ❌ Algunos tests de seguridad fallaron!
    echo Revisa los logs arriba para mas detalles.
    echo.
)

pause
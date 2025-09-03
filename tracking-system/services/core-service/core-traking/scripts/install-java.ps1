# =====================================================
# Script para descargar e instalar Java 17 automaticamente
# =====================================================

Write-Host "Descargando e instalando Java 17 (Eclipse Temurin)..." -ForegroundColor Green
Write-Host ""

# URL de descarga de Eclipse Temurin JDK 17
$javaUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.16%2B8/OpenJDK17U-jdk_x64_windows_hotspot_17.0.16_8.msi"
$installerPath = "$env:TEMP\temurin-17-jdk.msi"

try {
    # Descargar el instalador
    Write-Host "Descargando Java 17 JDK..." -ForegroundColor Yellow
    Write-Host "   URL: $javaUrl"
    Write-Host "   Destino: $installerPath"
    
    # Usar Invoke-WebRequest para descargar
    $ProgressPreference = 'SilentlyContinue'  # Ocultar barra de progreso para mejor rendimiento
    Invoke-WebRequest -Uri $javaUrl -OutFile $installerPath -UseBasicParsing
    
    Write-Host "✅ Descarga completada" -ForegroundColor Green
    
    # Verificar que el archivo se descargó
    if (Test-Path $installerPath) {
        $fileSize = (Get-Item $installerPath).Length / 1MB
        Write-Host "   Tamaño del archivo: $([math]::Round($fileSize, 2)) MB"
    } else {
        throw "El archivo no se descargó correctamente"
    }
    
    # Ejecutar el instalador
    Write-Host ""
    Write-Host "🚀 Ejecutando instalador..." -ForegroundColor Yellow
    Write-Host "   IMPORTANTE: Se abrirá el instalador de Java"
    Write-Host "   ASEGÚRATE de marcar la opción 'Set JAVA_HOME variable'"
    Write-Host ""
    
    # Ejecutar el instalador MSI
    Start-Process -FilePath "msiexec.exe" -ArgumentList "/i", $installerPath, "/quiet", "ADDLOCAL=FeatureMain,FeatureEnvironment,FeatureJarFileRunWith,FeatureJavaHome" -Wait
    
    Write-Host "✅ Instalación completada" -ForegroundColor Green
    
    # Limpiar archivo temporal
    Remove-Item $installerPath -Force -ErrorAction SilentlyContinue
    
    Write-Host ""
    Write-Host "🔧 Configurando variables de entorno..." -ForegroundColor Yellow
    
    # Buscar la instalación de Java
    $possiblePaths = @(
        "C:\Program Files\Eclipse Adoptium\jdk-17*",
        "C:\Program Files\Java\jdk-17*",
        "C:\Program Files (x86)\Eclipse Adoptium\jdk-17*"
    )
    
    $javaHome = $null
    foreach ($path in $possiblePaths) {
        $found = Get-ChildItem -Path $path -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $javaHome = $found.FullName
            break
        }
    }
    
    if ($javaHome) {
        Write-Host "   Java encontrado en: $javaHome"
        
        # Configurar JAVA_HOME para la sesión actual
        $env:JAVA_HOME = $javaHome
        $env:PATH = "$javaHome\bin;$env:PATH"
        
        Write-Host "✅ Variables de entorno configuradas para esta sesión" -ForegroundColor Green
        Write-Host ""
        Write-Host "⚠️  IMPORTANTE: Para que los cambios sean permanentes," -ForegroundColor Yellow
        Write-Host "   reinicia PowerShell después de la instalación" -ForegroundColor Yellow
        
    } else {
        Write-Host "⚠️  No se pudo encontrar la instalación de Java automáticamente" -ForegroundColor Yellow
        Write-Host "   Verifica manualmente que Java se instaló correctamente"
    }
    
    Write-Host ""
    Write-Host "🧪 Verificando instalación..." -ForegroundColor Yellow
    
    # Intentar ejecutar java -version
    try {
        $javaVersion = & "$javaHome\bin\java.exe" -version 2>&1
        Write-Host "✅ Java instalado correctamente:" -ForegroundColor Green
        Write-Host $javaVersion[0] -ForegroundColor Cyan
    } catch {
        Write-Host "⚠️  No se pudo verificar la instalación automáticamente" -ForegroundColor Yellow
        Write-Host "   Reinicia PowerShell e intenta: java -version"
    }
    
    Write-Host ""
    Write-Host "🎉 ¡Instalación completada!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📋 Próximos pasos:" -ForegroundColor Cyan
    Write-Host "   1. Reiniciar PowerShell"
    Write-Host "   2. Ejecutar: scripts\check-prerequisites.bat"
    Write-Host "   3. Si todo está verde, continuar con: scripts\start-db.bat"
    Write-Host ""
    
} catch {
    Write-Host ""
    Write-Host "❌ Error durante la instalación:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Alternativas:" -ForegroundColor Yellow
    Write-Host "   1. Descargar manualmente desde: https://adoptium.net/temurin/releases/"
    Write-Host "   2. Ejecutar PowerShell como Administrador e intentar: choco install temurin17 -y"
    Write-Host "   3. Seguir las instrucciones en SETUP-JAVA.md"
    Write-Host ""
}

Write-Host "Presiona cualquier tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
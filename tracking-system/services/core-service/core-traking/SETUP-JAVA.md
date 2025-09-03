# ☕ Instalación de Java 17 para Core Tracking Service

## 🎯 Prerrequisito Necesario

Para ejecutar el Core Tracking Service necesitas **Java 17** o superior instalado en tu sistema.

## 📥 Opciones de Instalación

### Opción 1: Eclipse Temurin (Recomendado)

1. **Ir a**: https://adoptium.net/temurin/releases/
2. **Seleccionar**:
   - Version: **17 - LTS**
   - Operating System: **Windows**
   - Architecture: **x64**
   - Package Type: **JDK**
3. **Descargar** el archivo `.msi`
4. **Ejecutar** el instalador y seguir las instrucciones
5. **Marcar** la opción "Set JAVA_HOME variable" durante la instalación

### Opción 2: Oracle JDK

1. **Ir a**: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. **Descargar** Java SE Development Kit 17
3. **Ejecutar** el instalador
4. **Configurar** JAVA_HOME manualmente (ver abajo)

### Opción 3: OpenJDK (Manual)

1. **Ir a**: https://jdk.java.net/17/
2. **Descargar** el ZIP para Windows
3. **Extraer** en `C:\Program Files\Java\jdk-17`
4. **Configurar** JAVA_HOME manualmente (ver abajo)

## 🔧 Configurar Variables de Entorno

Si el instalador no configuró automáticamente JAVA_HOME:

### Windows 10/11:

1. **Abrir** "Configuración del sistema" (Win + R, escribir `sysdm.cpl`)
2. **Ir** a la pestaña "Opciones avanzadas"
3. **Hacer clic** en "Variables de entorno"
4. **En "Variables del sistema"**, hacer clic en "Nueva"
5. **Agregar**:
   - Nombre: `JAVA_HOME`
   - Valor: `C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot` (o la ruta donde se instaló)
6. **Buscar** la variable `Path` en "Variables del sistema"
7. **Hacer clic** en "Editar"
8. **Agregar** una nueva entrada: `%JAVA_HOME%\bin`
9. **Hacer clic** en "Aceptar" en todas las ventanas

### PowerShell (Temporal):

```powershell
# Configurar para la sesión actual
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

## ✅ Verificar Instalación

Abrir una **nueva** ventana de PowerShell o CMD y ejecutar:

```bash
java -version
```

**Resultado esperado:**
```
openjdk version "17.0.x" 2023-xx-xx
OpenJDK Runtime Environment Temurin-17.0.x+x (build 17.0.x+x)
OpenJDK 64-Bit Server VM Temurin-17.0.x+x (build 17.0.x+x, mixed mode, sharing)
```

También verificar JAVA_HOME:
```bash
echo $env:JAVA_HOME
```

## 🚀 Después de Instalar Java

Una vez que Java esté instalado y configurado:

1. **Reiniciar** PowerShell/CMD
2. **Verificar** que `java -version` funcione
3. **Continuar** con las pruebas del sistema:

```bash
# Probar Maven wrapper
mvnw.cmd --version

# Si funciona, continuar con la aplicación
mvnw.cmd quarkus:dev
```

## 🔧 Troubleshooting

### Error: "JAVA_HOME not found"
- Verificar que JAVA_HOME esté configurado: `echo $env:JAVA_HOME`
- Reiniciar PowerShell después de configurar variables
- Verificar que la ruta sea correcta y exista

### Error: "java command not found"
- Verificar que `%JAVA_HOME%\bin` esté en el PATH
- Reiniciar PowerShell
- Probar con la ruta completa: `"C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot\bin\java.exe" -version`

### Error: "Unsupported Java version"
- Verificar que sea Java 17 o superior: `java -version`
- Si tienes múltiples versiones, asegurar que JAVA_HOME apunte a Java 17+

## 📋 Checklist de Verificación

- [ ] Java 17+ instalado
- [ ] `java -version` funciona
- [ ] JAVA_HOME configurado
- [ ] PATH incluye %JAVA_HOME%\bin
- [ ] PowerShell reiniciado
- [ ] `mvnw.cmd --version` funciona

## 🎯 Siguiente Paso

Una vez que Java esté funcionando:
1. **Volver** al archivo `TESTING-GUIDE.md`
2. **Continuar** con el Paso 2: Iniciar Base de Datos
3. **Ejecutar** `mvnw.cmd quarkus:dev` para iniciar la aplicación
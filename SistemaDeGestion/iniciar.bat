@echo off
setlocal enabledelayedexpansion

echo ========================================
   Hambur-Besa - Configuracion del servidor
echo ========================================
echo.

REM Verificar si existe .env
if not exist "%~dp0.env" (
    echo No se encontro el archivo .env
    echo Por favor ingresa las credenciales de la base de datos.
    echo.
    set /p DB_URL="URL de PostgreSQL (jdbc:postgresql://...): "
    set /p DB_USERNAME="Usuario de la base de datos: "
    set /p DB_PASSWORD="Password de la base de datos: "
    set /p JWT_SECRET="Clave secreta JWT (min 32 caracteres): "
    set /p JWT_EXPIRATION="Expiracion del JWT en segundos (default 86400): "

    if not "!DB_URL:~0,5!"=="jdbc:" set "DB_URL=jdbc:!DB_URL!"
    if "!JWT_EXPIRATION!"=="" set JWT_EXPIRATION=86400

    (
        echo DB_URL=!DB_URL!
        echo DB_USERNAME=!DB_USERNAME!
        echo DB_PASSWORD=!DB_PASSWORD!
        echo JWT_SECRET=!JWT_SECRET!
        echo JWT_EXPIRATION=!JWT_EXPIRATION!
    ) > "%~dp0.env"

    echo.
    echo [OK] Archivo .env creado correctamente
    echo.
)

REM Cargar variables desde .env
for /f "usebackq tokens=1,* delims==" %%A in ("%~dp0.env") do (
    set "%%A=%%B"
)

echo [OK] Variables de entorno cargadas
echo.
echo   DB_URL:      !DB_URL:~0,50!
echo   DB_USERNAME: !DB_USERNAME!
echo   DB_PASSWORD: ****
echo   JWT_EXPIRATION: !JWT_EXPIRATION!s
echo.

REM Iniciar backend en una ventana nueva
echo Iniciando backend (Spring Boot)...
start "Hambur-Besa Backend" cmd /k "cd /d %~dp0 && call mvnw spring-boot:run"

REM Esperar a que el backend arranque
echo Esperando a que el backend arranque...
timeout /t 20 /nobreak >nul

REM Iniciar frontend en otra ventana nueva
echo Iniciando frontend (Angular)...
start "Hambur-Besa Frontend" cmd /k "cd /d %~dp0../frontend && npm start"

echo.
echo ========================================
   Servidor iniciado correctamente
echo ========================================
echo   Backend:  http://localhost:8080
echo   Frontend: http://localhost:4200
echo ========================================
echo.
echo Cerrá esta ventana cuando quieras.
echo Para detener el servidor, cerrá las ventanas de Backend y Frontend.
echo.

pause

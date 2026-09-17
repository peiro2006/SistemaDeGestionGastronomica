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
echo Iniciando Spring Boot...
echo.

cd /d "%~dp0"
call mvnw spring-boot:run

pause

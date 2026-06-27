@echo off
setlocal

cd /d "%~dp0"

if "%DB_USERNAME%"=="" set "DB_USERNAME=root"
if "%SERVER_PORT%"=="" set "SERVER_PORT=8091"
if "%DB_PASSWORD%"=="" (
    set /p "DB_PASSWORD=Escribe la clave de MySQL del usuario %DB_USERNAME%: "
)

if "%DB_PASSWORD%"=="" (
    echo.
    echo ERROR: Debes indicar la clave de MySQL.
    pause
    exit /b 1
)

echo.
echo Iniciando Hotel Exclusive en el puerto %SERVER_PORT%...
call mvnw.cmd spring-boot:run

endlocal

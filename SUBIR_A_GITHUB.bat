@echo off
setlocal EnableExtensions
cd /d "%~dp0"

set "REPO_URL=https://github.com/Leandro1414/ProyectoHotelLP2.git"

where git >nul 2>nul
if errorlevel 1 (
    echo ERROR: Git no esta instalado o no se encuentra en PATH.
    echo Descargalo desde https://git-scm.com/download/win
    pause
    exit /b 1
)

for /f "delims=" %%A in ('git config --global user.name 2^>nul') do set "GIT_NAME=%%A"
if not defined GIT_NAME (
    set /p "GIT_NAME=Escribe el nombre que usaras en Git: "
    git config --global user.name "%GIT_NAME%"
)

for /f "delims=" %%A in ('git config --global user.email 2^>nul') do set "GIT_EMAIL=%%A"
if not defined GIT_EMAIL (
    set /p "GIT_EMAIL=Escribe el correo asociado a GitHub: "
    git config --global user.email "%GIT_EMAIL%"
)

if not exist ".git" git init

git branch -M main
git add .

git diff --cached --quiet
if errorlevel 1 (
    git commit -m "Version inicial del sistema de reservas de hotel"
) else (
    echo No hay cambios nuevos para crear un commit.
)

git remote get-url origin >nul 2>nul
if errorlevel 1 (
    git remote add origin "%REPO_URL%"
) else (
    git remote set-url origin "%REPO_URL%"
)

echo.
echo Se intentara subir a:
echo %REPO_URL%
echo.
git push -u origin main

if errorlevel 1 (
    echo.
    echo No se pudo completar el push.
    echo Verifica que el repositorio ProyectoHotelLP2 exista en tu cuenta y que hayas iniciado sesion en GitHub.
    pause
    exit /b 1
)

echo.
echo Proyecto publicado correctamente.
pause
endlocal

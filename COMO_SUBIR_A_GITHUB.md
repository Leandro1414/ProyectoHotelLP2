# Cómo publicar el proyecto en GitHub

Cuenta identificada: `Leandro1414`

Repositorio recomendado: `ProyectoHotelLP2`

## Paso 1: crear el repositorio vacío

1. Ingrese a `https://github.com/Leandro1414`.
2. Abra la pestaña **Repositories**.
3. Pulse **New**.
4. Escriba `ProyectoHotelLP2`.
5. Seleccione **Public** o **Private**, según indique el profesor.
6. No marque **Add a README file**, **Add .gitignore** ni **Choose a license**.
7. Pulse **Create repository**.

## Paso 2: publicar con el archivo automático

Desde la carpeta descomprimida ejecute:

```text
SUBIR_A_GITHUB.bat
```

El archivo:

- inicializa Git;
- agrega los archivos permitidos;
- crea el primer commit;
- configura la rama `main`;
- conecta `origin` con `https://github.com/Leandro1414/ProyectoHotelLP2.git`;
- ejecuta el primer `push`.

GitHub puede abrir el navegador para solicitar autorización.

## Alternativa con Git Bash

```bash
git init
git branch -M main
git add .
git status
git commit -m "Version inicial del sistema de reservas de hotel"
git remote add origin https://github.com/Leandro1414/ProyectoHotelLP2.git
git push -u origin main
```

## Después del primer push

Abra en GitHub:

```text
Actions → CI - Maven
```

La ejecución debe compilar el proyecto y ejecutar sus pruebas. Si finaliza correctamente aparecerá una marca verde.

## Subir cambios posteriores

```bash
git add .
git commit -m "Describe brevemente el cambio"
git push
```

## Seguridad

No agregue contraseñas a `application.properties`. La aplicación espera la variable `DB_PASSWORD` y el archivo `.gitignore` evita subir archivos locales como `.env` o `application-local.properties`.

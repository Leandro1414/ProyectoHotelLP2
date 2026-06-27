# Hotel Exclusive — Proyecto LP2

[![CI - Maven](https://github.com/Leandro1414/ProyectoHotelLP2/actions/workflows/ci.yml/badge.svg)](https://github.com/Leandro1414/ProyectoHotelLP2/actions/workflows/ci.yml)

Sistema web de reservas de hotel desarrollado con Java 17, Spring Boot, Thymeleaf, Spring Security, JPA/Hibernate, MySQL y JasperReports.

## Funcionalidades

- Inicio y cierre de sesión con roles `ADMIN` y `RECEPCIONISTA`.
- Contraseñas cifradas con BCrypt.
- CRUD de clientes, empleados, tipos de habitación, habitaciones y servicios.
- Registro de reservas con una o varias habitaciones.
- Validación de fechas, capacidad y cruces de disponibilidad.
- Cálculo automático del alojamiento y los consumos.
- Cancelación lógica de reservas para conservar el historial.
- Transacciones con `@Transactional` y rollback ante errores.
- Reportes PDF con JasperReports:
  - Reservas registradas hoy.
  - Consumos registrados hoy.
  - Comprobante individual de reserva.
- Pruebas de integración con H2.
- Integración continua con GitHub Actions.
- Flujo manual preparado para despliegue en Azure App Service.

## Tecnologías

- Java 17
- Spring Boot 3.5.14
- Spring MVC
- Spring Data JPA / Hibernate
- Spring Security
- Thymeleaf
- MySQL 8
- JasperReports 6.21.3
- Maven Wrapper
- JUnit 5, Spring Boot Test y H2

## Estructura

```text
src/main/java/com/cibertec/hotel
├── config       Configuración de seguridad y carga de usuarios
├── controller   Controladores MVC y endpoints
├── dto          Formularios y objetos de transferencia
├── entity       Entidades JPA y enumeraciones
├── exception    Excepciones de negocio
├── repository   Acceso a datos con Spring Data JPA
└── service      Interfaces, reglas de negocio y transacciones

src/main/resources
├── reportes     Plantillas JasperReports JRXML
├── static       CSS y JavaScript
├── templates    Vistas Thymeleaf
└── application.properties
```

## 1. Crear la base de datos

Instalación nueva:

```text
database/db_ReservasHotel.sql
```

> Este script elimina y vuelve a crear la base `LP2Final`.

Actualización desde la versión V5 sin borrar datos:

```text
database/db_Reportes_V6.sql
```

## 2. Configurar la conexión

La contraseña no está guardada en el repositorio. Configure estas variables en Spring Tool Suite, Windows, Linux o el servicio de despliegue:

```text
DB_URL=jdbc:mysql://localhost:3306/LP2Final?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
DB_USERNAME=root
DB_PASSWORD=TU_CLAVE_MYSQL
SERVER_PORT=8091
```

En Spring Tool Suite:

1. Abra **Run > Run Configurations**.
2. Seleccione la aplicación Spring Boot.
3. Abra la pestaña **Environment**.
4. Agregue `DB_PASSWORD` y, cuando corresponda, `DB_USERNAME`, `DB_URL` y `SERVER_PORT`.

También puede ejecutar `INICIAR_PROYECTO.bat`; el script pedirá la clave sin almacenarla.

## 3. Ejecutar

```bash
./mvnw spring-boot:run
```

En Windows:

```bat
mvnw.cmd spring-boot:run
```

Abra:

```text
http://localhost:8091
```

Cuenta demostrativa inicial:

```text
Correo: admin@hotel.com
Contraseña: Admin123*
```

> Cambie esta contraseña antes de utilizar la aplicación fuera de un entorno académico.

## 4. Pruebas

```bash
./mvnw clean verify
```

Las pruebas usan H2 y no requieren una instalación local de MySQL.

## Reportes

```text
/reportes
/reportes/reservas-hoy
/reportes/consumos-hoy
/reportes/comprobante/{idReserva}
```

Agregue `?modo=descargar` para descargar directamente un PDF.

## Transacciones

La lógica transaccional se encuentra en la capa de servicios. El registro de una reserva valida fechas, cliente, empleado, habitaciones disponibles y capacidad; después guarda la reserva, sus detalles, precios históricos, comprobante y monto total dentro de una misma transacción.

Si alguna operación lanza una excepción de negocio o de persistencia, Spring ejecuta rollback y evita que la base de datos quede con información parcial.

## GitHub Actions

El workflow `.github/workflows/ci.yml` se ejecuta en cada `push` o `pull_request` hacia `main`:

1. Configura Java 17.
2. Ejecuta `clean verify`.
3. Compila las pruebas.
4. Publica el archivo WAR como artefacto de la ejecución.

El workflow `.github/workflows/azure-deploy.yml` permite un despliegue manual en Azure. Antes de usarlo debe crear el secreto:

```text
AZURE_WEBAPP_PUBLISH_PROFILE
```

## Publicar este proyecto en GitHub

1. En la cuenta [Leandro1414](https://github.com/Leandro1414), pulse **New**.
2. Cree un repositorio vacío llamado `ProyectoHotelLP2`.
3. No agregue README, `.gitignore` ni licencia desde GitHub; este proyecto ya los contiene.
4. Desde esta carpeta ejecute `SUBIR_A_GITHUB.bat`.

También puede usar Git Bash:

```bash
git init
git branch -M main
git add .
git commit -m "Version inicial del sistema de reservas de hotel"
git remote add origin https://github.com/Leandro1414/ProyectoHotelLP2.git
git push -u origin main
```

GitHub puede abrir el navegador para solicitar autorización. No use su contraseña normal como contraseña de Git; utilice el inicio de sesión del navegador o un token personal cuando GitHub lo solicite.

## Licencia y uso

Proyecto académico de Lenguaje de Programación II. Antes de utilizarlo en producción deben revisarse credenciales, permisos, respaldos, concurrencia y configuración de infraestructura.

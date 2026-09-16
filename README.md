# ExpresoFast

Aplicación web para gestión de envíos, logística y auditoría de estados, desarrollada con Spring Boot, JPA y JavaScript vanilla.

## Requisitos

- Java 17+
- Maven
- SQL Server 2019+
- Git

## Configuración local

1. Crear la base de datos `ExpresoFast_c36342_II2026` en SQL Server.
2. Ajustar la conexión en `backend/src/main/resources/application.properties` localmente.
3. Ejecutar los scripts SQL en el orden:
   - `database/01_schema_lab5.sql`
   - `database/02_schema_lab6_extension.sql`
   - `database/03_data_seeds.sql`

## Ejecutar la aplicación

```bash
cd backend
./mvnw spring-boot:run
```

La aplicación queda disponible en:

- Frontend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Usuarios de prueba

Los scripts de datos crean usuarios con roles como `ADMIN`, `OPERADOR` y `CONDUCTOR`.

## Colección de Postman

Importar la colección desde:

- `postman/ExpresoFast-Lab6.postman_collection.json`

## Estructura principal

- `backend/`: aplicación Spring Boot
- `frontend/`: UI estática con JavaScript
- `database/`: scripts SQL
- `postman/`: colección de Postman

## Funcionalidades

- Login con JWT
- Autenticación y autorización por roles
- Registro y consulta de envíos
- Cambio de estado con bitácora de auditoría
- Catálogos de vehículos, conductores y empresas

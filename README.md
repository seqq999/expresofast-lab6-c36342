# ExpresoFast — Laboratorio 10: SPA Angular Standalone + Backend RESTful por capas

Migración del cliente web de ExpresoFast a una Single Page Application en Angular
Standalone, consumiendo una API REST en Spring Boot organizada por capas
(modelo, repositorio, DTOs, servicio y controlador).

## Estructura del repositorio

```
expresofast-project/
├── expresofast-backend/    → API REST Spring Boot (Java 21)
└── expresofast-frontend/   → SPA Angular Standalone
```

## Requisitos previos

- Java 21+ y Maven (o el wrapper `mvnw` incluido)
- Node.js 18+ y Angular CLI (`npm install -g @angular/cli`)
- SQL Server (o el motor configurado en `application.properties`)

## Backend: cómo ejecutarlo

1. Ir a la carpeta del backend:
   ```bash
   cd expresofast-backend
   ```
2. Configurar la conexión a la base de datos en
   `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=TU_BASE;encrypt=true;trustServerCertificate=true
   spring.datasource.username=...
   spring.datasource.password=...
   ```
3. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
4. El API queda disponible en `http://localhost:8080/api/v1/envios`.
5. Documentación Swagger: `http://localhost:8080/swagger-ui/index.html`.

## Frontend: cómo ejecutarlo

1. Ir a la carpeta del frontend:
   ```bash
   cd expresofast-frontend
   ```
2. Instalar dependencias:
   ```bash
   npm install
   ```
3. Levantar el servidor de desarrollo:
   ```bash
   ng serve
   ```
4. Abrir `http://localhost:4200` en el navegador. El backend debe estar
   corriendo en `http://localhost:8080` (CORS ya está habilitado para
   `http://localhost:4200` en `EnvioController`).

## Vistas de la aplicación

| Ruta | Componente | Descripción |
|---|---|---|
| `/envios` | `EnvioListComponent` | Tabla de envíos con insignia de color por estado y selector para actualizar el estado directamente |
| `/nuevo-envio` | `EnvioFormComponent` | Formulario de registro de un nuevo envío (genera el código de rastreo automáticamente) |
| `/rastreo` | `EnvioTrackingComponent` | Búsqueda por código de rastreo con barra de progreso según el estado |

La ruta raíz (`/`) redirige a `/envios`.

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/envios` | Lista todos los envíos |
| GET | `/api/v1/envios/rastreo/{codigo}` | Detalle de un envío por código de rastreo |
| POST | `/api/v1/envios` | Registra un nuevo envío |
| PATCH | `/api/v1/envios/{id}/estado?estado=EN_TRANSITO` | Actualiza el estado de un envío |

## Estados posibles

`PENDIENTE`, `EN_TRANSITO`, `ENTREGADO`, `CANCELADO`
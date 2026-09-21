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

## Suite de Pruebas (Laboratorio 7)

El proyecto cuenta con una suite de pruebas automatizadas que cubre la capa de
servicios de negocio y la capa de controladores REST, con un umbral mínimo de
cobertura exigido mediante JaCoCo.

### Cómo ejecutar las pruebas

Ejecutar solo las pruebas (sin verificar el umbral de cobertura):

```bash
mvn clean test
```

Ejecutar el build completo con verificación de cobertura JaCoCo (falla el
build si la cobertura de instrucciones del paquete `business` es menor al
85%):

```bash
mvn clean verify
```

Un `mvn clean verify` exitoso finaliza con la leyenda `BUILD SUCCESS`.

### Ver el reporte de cobertura

Después de ejecutar `mvn clean verify` (o `mvn clean test` seguido de
`mvn jacoco:report`), abre el siguiente archivo en el navegador:

```
target/site/jacoco/index.html
```

Ahí se muestra el desglose de cobertura por paquete; el paquete
`cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business` (servicios de
negocio) es el que se certifica sobre el umbral mínimo del 85%.

### Estructura de las pruebas

- `backend/src/test/java/.../TestServices/`: pruebas unitarias de la capa de
  negocio con JUnit 5 y Mockito (`@ExtendWith(MockitoExtension.class)`),
  aislando por completo las dependencias de base de datos mediante mocks.
  Cubre `EnvioService`, `VehiculoService`, `AuthService`,
  `ConductorService` y `EmpresaLogisticaService`, incluyendo pruebas
  parametrizadas (`@ParameterizedTest` + `@CsvSource`) para el cálculo de
  tarifas de envío.
- `backend/src/test/java/.../TestController/`: pruebas de corte de
  controlador (`@WebMvcTest` + `MockMvc`) para `EnvioController` y
  `AuthController`, validando códigos de estado HTTP (200, 400, 401, 404) y
  la estructura de las respuestas JSON mediante `jsonPath`.

### Herramientas utilizadas

- JUnit 5 Jupiter
- Mockito 5.x
- Spring Boot Test (`@WebMvcTest`, `MockMvc`, `@MockitoBean`)
- JaCoCo (`jacoco-maven-plugin`) para el análisis de cobertura de código

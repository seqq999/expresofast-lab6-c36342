# ExpresoFast

Aplicación web para gestión de envíos, logística y auditoría de estados, desarrollada con Spring Boot, JPA y JavaScript vanilla.

## Requisitos

- Java 25 (definido en `pom.xml`) y Maven (o el wrapper `mvnw` incluido)
- Spring Boot 4.1.1
- SQL Server 2019+
- Git
- Un servidor estático para el cliente web (por ejemplo, extensión **Live Server** de VS Code)

## Configuración local

1. Crear la base de datos `ExpresoFast_c36342_II2026` en SQL Server.
2. Ajustar la conexión y el secreto JWT en `backend/src/main/resources/application.properties` localmente.
3. Ejecutar los scripts SQL **en este orden** (abrirlos como UTF-8 para conservar las tildes):
   - `database/01_schema_lab5.sql`
   - `database/02_schema_lab6_extension.sql`
   - `database/03_data_seeds.sql`
   - `database/04_lab9_stored_procedures.sql` (columna `destinatario`, índice y stored procedures)
   - `database/05_lab9_data_seeds.sql` (18 envíos de prueba con los 4 estados)

## Ejecutar el back-end (API REST)

El `pom.xml` y el wrapper `mvnw` están en la **raíz del repositorio** (no dentro de `backend/`), así que el comando se ejecuta desde la raíz:

```bash
./mvnw spring-boot:run
```

En Windows (PowerShell): `.\mvnw.cmd spring-boot:run`, o `mvn spring-boot:run` si tienes Maven instalado.

La API REST queda disponible en `http://localhost:8080/api`.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Ejecutar el cliente web (front-end)

El cliente es HTML5 + CSS3 + JavaScript puro y vive en la carpeta `frontend/`, independiente del backend.

1. Asegurarse de que el backend esté corriendo en `http://localhost:8080` (paso anterior).
2. Abrir la carpeta `frontend/` en Visual Studio Code.
3. Click derecho sobre `index.html` → **Open with Live Server**.
4. El cliente queda disponible en `http://127.0.0.1:5500/index.html` (o el puerto que use tu Live Server).
5. Iniciar sesión. Desde la consola (`dashboard.html`), el botón **☰ Envíos paginados** abre la vista paginada del Laboratorio 9.

> Abrir la vista paginada desde ese botón, en la misma pestaña: el token JWT vive en `sessionStorage`, que es propio de cada pestaña.

> Si usas un puerto distinto a `5500`, actualiza los orígenes permitidos en `WebConfig` (backend) para que coincida con el origen real del cliente.

## Usuarios de prueba

| Usuario   | Contraseña    | Rol            |
| --------- | ------------- | -------------- |
| admin     | admin1234     | ROLE_ADMIN     |
| operador  | ope1234       | ROLE_OPERADOR  |
| conductor | conductor1234 | ROLE_CONDUCTOR |

Los scripts de datos crean estos usuarios con sus roles correspondientes (`ADMIN`, `OPERADOR`, `CONDUCTOR`).

## Colección de Postman

Importar la colección desde:

- `postman/ExpresoFast-Lab6.postman_collection.json`

## Estructura principal

- `backend/`: código fuente de la aplicación Spring Boot (API REST)
- `frontend/`: cliente web estático (HTML5 semántico, CSS3 responsivo, JavaScript)
  - `frontend/paginado/`: vista paginada del Laboratorio 9
- `database/`: scripts SQL
- `postman/`: colección de Postman

## Funcionalidades

- Login con JWT
- Autenticación y autorización por roles (RBAC)
- Registro y consulta de envíos
- Cambio de estado con bitácora de auditoría
- Catálogos de vehículos, conductores y empresas
- Consola web (Laboratorio 8) que consume la API mediante Fetch API/async-await, con:
  - Token JWT almacenado en `sessionStorage` y decodificado en el cliente
  - Renderizado dinámico según rol:
    - **ROLE_ADMIN**: bitácora de auditoría, registrar vehículo, control total de estados
    - **ROLE_OPERADOR**: asignar vehículo y avanzar envíos a `EN_TRANSITO`
    - **ROLE_CONDUCTOR**: solo ve sus envíos asignados y puede marcarlos como `ENTREGADO`
  - Manejo de errores HTTP 400 (RFC 7807), 401 y 403
  - Diseño responsivo (Mobile-First) con CSS Grid y Flexbox
- Stored procedures, paginación relacional y vista paginada (Laboratorio 9, ver sección siguiente)

## Stored Procedures y Paginación (Laboratorio 9)

### Stored procedures (`database/04_lab9_stored_procedures.sql`)

| Procedimiento                                | Parámetros             | Resultado                                                        |
| -------------------------------------------- | ---------------------- | ---------------------------------------------------------------- |
| `SP_OBTENER_ENVIOS_POR_ESTADO`               | `@pEstado VARCHAR(20)` | Envíos de ese estado, ordenados por `fecha_creacion` descendente |
| `SP_RESUMEN_METRICAS_ENVIOS` (reto opcional) | —                      | Conteo de envíos y suma de flete agrupados por estado            |

Se invocan desde Spring Data JPA con `@Procedure(procedureName = "SP_OBTENER_ENVIOS_POR_ESTADO")` y `@Param("pEstado")` en `EnvioRepository`. El método del servicio que lo llama (`listarViaStoredProcedure`) usa `@Transactional(readOnly = true)`: Spring Data lo exige para poder consumir el `ResultSet` del procedimiento.

### Endpoints

Todos requieren el header `Authorization: Bearer <token>`.

| Método | Ruta                                    | Descripción                                            |
| ------ | --------------------------------------- | ------------------------------------------------------ |
| GET    | `/api/v1/envios`                        | Lista paginada de envíos (`Page<EnvioDTO>`)            |
| GET    | `/api/v1/envios/procedimiento/{estado}` | Envíos de un estado vía stored procedure (sin paginar) |

Parámetros de `GET /api/v1/envios` (todos opcionales):

| Parámetro   | Default         | Descripción                                                   |
| ----------- | --------------- | ------------------------------------------------------------- |
| `page`      | `0`             | Número de página, base 0 (igual que Spring Data)              |
| `size`      | `5`             | Ítems por página                                              |
| `sortBy`    | `fechaCreacion` | Propiedad de la entidad por la que se ordena                  |
| `direction` | `desc`          | `asc` o `desc`                                                |
| `busqueda`  | —               | Texto a buscar en código de rastreo, destinatario o dirección |
| `estado`    | —               | `PENDIENTE`, `EN_TRANSITO`, `ENTREGADO` o `CANCELADO`         |

Ejemplo:

```
GET /api/v1/envios?page=0&size=10&estado=PENDIENTE&busqueda=cartago
```

La respuesta incluye `content`, `number`, `size`, `totalElements`, `totalPages`, `first` y `last`.

### Detalles de implementación

- **Paginación física:** las consultas usan `Pageable`/`PageRequest`, así que el `LIMIT/OFFSET` se aplica en SQL y no se cargan todos los registros en memoria.
- **Sin advertencia `HHH000104`:** la consulta paginada `buscarPaginado` carga las relaciones con `@EntityGraph` en lugar de `JOIN FETCH`.
- **Índice base 0:** el cliente envía `page` en base 0 y muestra `data.number + 1` al usuario.
- **`EnvioDTO`:** `id`, `codigoRastreo`, `destinatario`, `direccionDestino`, `montoFlete`, `estado` y `fechaCreacion`. `montoFlete` se lee de la columna `costo`.

### Vista paginada (`frontend/paginado/dashboard-paginado.html`)

- Barra de filtros: búsqueda de texto, origen de datos (consulta paginada JPA o stored procedure), estado y tamaño de página (5, 10 o 20).
- Tabla semántica con Rastreo, Destinatario, Dirección, Flete y Estado.
- Navegación: **« Primera**, **‹ Anterior**, **Siguiente ›**, **Última »** y el indicador «Página X de Y (Total: Z envíos)». Los botones se deshabilitan según `data.first` y `data.last`.
- En modo _Stored Procedure_ hay que elegir un estado; el resultado no se pagina y la barra de navegación se oculta.

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

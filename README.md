# ExpresoFast — Laboratorio 11: SPA Angular Standalone + Backend RESTful por capas

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
| `/envio-avanzado` | `EnvioAvanzadoFormComponent` | Formulario reactivo para registrar un envío con fechas, validación de tracking y una lista dinámica de paquetes |
| `/rastreo` | `EnvioTrackingComponent` | Búsqueda por código de rastreo con barra de progreso según el estado |

La ruta raíz (`/`) redirige a `/envios`.

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/envios` | Lista todos los envíos |
| GET | `/api/v1/envios/rastreo/{codigo}` | Detalle de un envío por código de rastreo |
| POST | `/api/v1/envios` | Registra un nuevo envío |
| PATCH | `/api/v1/envios/{id}/estado?estado=EN_TRANSITO` | Actualiza el estado de un envío |
| GET | `/api/v1/envios/check-tracking/{trackingNumber}` | Verifica si un código de rastreo ya está registrado |

## Estados posibles

`PENDIENTE`, `EN_TRANSITO`, `ENTREGADO`, `CANCELADO`

## Fundamentación teórica

### 1. UX y escalabilidad: FormArray y formularios reactivos

En el formulario avanzado, los paquetes se representan mediante un `FormArray` de
`FormGroup`. Cada `FormGroup` contiene los controles de descripción y peso de un
paquete. Esta estructura es preferible a crear diez campos de texto estáticos y
ocultos por varias razones técnicas:

- **UX dinámica:** el usuario puede añadir únicamente los paquetes que necesita y
  eliminar los que ya no requiere. La interfaz no muestra controles innecesarios ni
  obliga a recorrer diez campos vacíos. Además, cada control puede mostrar su estado
  de validación de forma inmediata.
- **Validación consistente:** los controles se crean usando la misma función
  (`crearPaqueteGroup`), por lo que todos aplican las mismas reglas, como descripción
  obligatoria y peso mayor que cero. Esto evita que algunos campos estáticos tengan
  validaciones diferentes u olvidadas.
- **Modelo de datos alineado:** el valor de `FormArray` ya tiene la forma de una
  colección de paquetes (`paquetes: Paquete[]`). Esto facilita construir el payload
  que se envía al backend sin leer manualmente diez elementos ni filtrar campos
  ocultos.
- **Mantenibilidad:** la cantidad de paquetes deja de estar codificada en el HTML.
  Si el negocio cambia el límite o se agregan nuevos atributos a un paquete, el
  cambio se realiza en la función que crea el grupo y en la plantilla repetitiva,
  en lugar de duplicar y mantener diez bloques independientes.
- **Escalabilidad:** `FormArray` permite crecer o reducir la colección en tiempo de
  ejecución con `push` y `removeAt`. El mismo patrón funciona para pocos paquetes o
  para una cantidad mayor, sin aumentar proporcionalmente el código de la vista.
  Los formularios reactivos también centralizan el estado, las validaciones y el
  envío, lo cual hace más predecible el comportamiento de la aplicación.

En contraste, diez campos estáticos y ocultos generan una interfaz rígida: reservan
controles que posiblemente no se usan, complican la accesibilidad, requieren lógica
adicional para saber cuáles están activos y favorecen inconsistencias entre campos.
Ocultar elementos con HTML no constituye una colección dinámica ni resuelve por sí
solo su ciclo de validación y serialización.

### 2. Ciclo de eventos: validadores síncronos y asíncronos

JavaScript ejecuta el código mediante una pila de llamadas (*call stack*) y un
**Event Loop**. Las operaciones asíncronas, como una solicitud HTTP, no bloquean la
pila: cuando terminan, su resultado se programa para ser procesado posteriormente
por el Event Loop.

El validador cruzado `fechasValidator` es **síncrono**. Angular lo ejecuta
directamente durante la evaluación del formulario: lee `fechaDespacho` y
`fechaEntregaEstimada`, compara sus valores y retorna inmediatamente `null` si son
válidos o `{ fechasInvalidas: true }` si la fecha de entrega no es posterior. No
hay espera ni trabajo fuera de la pila de llamadas, porque toda la información
necesaria ya está disponible en el navegador.

El validador `trackingDuplicadoValidator` es **asíncrono**. Después de un
`debounceTime`, solicita al backend si el código de rastreo ya existe. La respuesta
HTTP llega más tarde, por lo que Angular no puede recibir el resultado como un
objeto inmediato. El validador retorna un `Observable` que emite `null` cuando el
tracking está disponible o `{ trackingTomado: true }` cuando ya está registrado.
El `Observable` permite representar ese resultado futuro, actualizar el estado
`pending` del control y notificar a Angular cuando termina la validación. Angular
también acepta una `Promise` por la misma razón: ambos tipos representan una
operación cuyo resultado estará disponible después.

Por eso un validador asíncrono no debe retornar directamente un booleano o un
objeto de error. Si lo hiciera, Angular tendría que asumir que la validación ya
terminó y podría habilitar el envío antes de recibir la respuesta del servidor,
permitiendo duplicar códigos de rastreo. El `Observable` o la `Promise` hacen
explícita la naturaleza asíncrona del proceso y permiten que el formulario
permanezca en estado `PENDING` hasta obtener una respuesta.
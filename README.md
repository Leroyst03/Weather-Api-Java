# ESP32 Weather Station — Backend

API REST + WebSocket construida con **Spring Boot** que recibe mediciones del ESP32, las persiste en base de datos y las retransmite en tiempo real al dashboard web.

---

## Tecnologias

| Tecnologia        | Uso                                      |
|-------------------|------------------------------------------|
| Spring Boot       | Framework principal                      |
| Spring Web        | API REST (`MeasurementController`)       |
| Spring WebSocket  | Broker STOMP para tiempo real            |
| Spring Data JPA   | Persistencia con `DataRepository`        |
| Thymeleaf         | Renderizado del dashboard HTML           |
| Maven             | Gestion de dependencias y build          |

---

## Estructura del proyecto

```
src/main/java/weather/api/app/
├── AppApplication.java          # Punto de entrada Spring Boot
├── Config/
│   └── WebSocketConfig.java     # Configuracion STOMP y SockJS
├── Controller/
│   ├── MeasurementController.java  # POST /measurements
│   └── DashboardController.java    # GET / -> dashboard.html
├── DTO/
│   └── DataDTO.java             # Objeto de transferencia (entrada del ESP32)
├── Model/
│   └── Data.java                # Entidad JPA (tabla: data)
├── Repository/
│   └── DataRepository.java      # Acceso a base de datos
└── Service/
    └── DataService.java         # Logica de negocio

src/main/resources/
├── application.properties       # Configuracion de BD y servidor
├── templates/
│   └── dashboard.html           # Vista Thymeleaf
└── static/css/
    └── dashboard.css            # Estilos del dashboard
```

---

## Flujo de datos

```
ESP32
  │
  │  POST /measurements
  │  { "temperature": 24.5, "humidity": 61.0 }
  ▼
MeasurementController
  │
  ├──► DataService.saveFromDTO()  ──►  DataRepository  ──►  Base de datos
  │
  └──► SimpMessagingTemplate
         │
         │  /topic/live
         ▼
      Dashboard Web (WebSocket)
```

---

## API REST

### `POST /measurements`

Recibe una medicion del ESP32 y la guarda en base de datos. Tras guardarla, la retransmite por WebSocket al topic `/topic/live`.

**Request body:**
```json
{
  "temperature": 24.5,
  "humidity": 61.0,
  "deviceId": "esp32-01"
}
```

**Response:** `200 OK`

---

## WebSocket

| Parametro          | Valor             |
|--------------------|-------------------|
| Endpoint SockJS    | `/ws`             |
| Topic de emision   | `/topic/live`     |
| Prefijo app        | `/app`            |
| Origenes permitidos| `*`               |

El dashboard se suscribe a `/topic/live` y recibe cada medicion nueva en tiempo real en cuanto el ESP32 hace un POST.

---

## Modelo de datos

La entidad `Data` se persiste en la tabla `data`:

| Campo       | Tipo    | Descripcion                  |
|-------------|---------|------------------------------|
| `id`        | Long    | Clave primaria autoincremental|
| `device_id` | String  | Identificador del dispositivo|
| `temperature`| Double | Temperatura en grados Celsius|
| `humidity`  | Double  | Humedad relativa en %        |

---

## Configuracion

Edita `src/main/resources/application.properties`:

```properties
# Puerto del servidor (por defecto 8080)
server.port=8080

# Base de datos (ejemplo con H2 en memoria para desarrollo)
spring.datasource.url=jdbc:h2:mem:weatherdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update

# Ejemplo con PostgreSQL para produccion
# spring.datasource.url=jdbc:postgresql://localhost:5432/weatherdb
# spring.datasource.username=usuario
# spring.datasource.password=contrasena
```

---

## Arrancar el servidor

**Con Maven Wrapper:**
```bash
./mvnw spring-boot:run
```

**Con el JAR compilado:**
```bash
java -jar target/app-0.0.1-SNAPSHOT.jar
```

El dashboard estara disponible en `http://localhost:8080`.

---


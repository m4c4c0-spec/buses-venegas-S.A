# buses_venegas_S.A

Sistema de gestión y reserva de pasajes de buses interurbanos, desarrollado con arquitectura Onion (Clean Architecture) para garantizar escalabilidad, mantenibilidad y testabilidad.
Tabla de Contenidos

1.Características
2.Arquitectura
3.Tecnologías
4.Requisitos Previos
5.Instalación
6.Configuración
7.Ejecución
8.Estructura del Proyecto
9.API Documentation
10.Testing
11.Roadmap
12.Equipo

Características
Funcionalidades Actuales

Gestión de Reservas: Creación, confirmación y cancelación de reservas de pasajes
Sistema de Retención de Asientos: Bloqueo temporal de asientos durante el proceso de reserva
Búsqueda de Viajes: Consulta de rutas disponibles por origen, destino y fecha
Gestión de Usuarios: Registro y administración de pasajeros
Procesamiento de Pagos: Integración con métodos de pago
Seguridad: Implementación con Spring Security
Limpieza Automática: Tarea programada para liberar asientos retenidos expirados

Funcionalidades Planificadas (Roadmap)

Seguimiento en Tiempo Real: Tracking de buses en ruta (microservicio con MongoDB)
Notificaciones Multicanal: Envío de pasajes por WhatsApp y correo electrónico
Dashboard Administrativo: Panel de control para gestión de flota y reportes

Arquitectura
Este proyecto implementa Onion Architecture (Arquitectura de Cebolla), una arquitectura limpia que garantiza:

-Independencia del framework
-Independencia de la base de datos
-Independencia de la UI
-Alta testabilidad
-Reglas de negocio protegidas

Estructura de Capas
┌─────────────────────────────────────┐
│     INTERFACES (Layer 4)            │
│     Controllers, DTOs, REST API     │
├─────────────────────────────────────┤
│   INFRASTRUCTURE (Layer 3)          │
│   JPA, Redis, PostgreSQL, Config    │
├─────────────────────────────────────┤
│    APPLICATION (Layer 2)            │
│    Use Cases, Services, Tasks       │
├─────────────────────────────────────┤
│      DOMAIN (Layer 1 - Core)        │
│   Entities, Value Objects, Repos    │
└─────────────────────────────────────┘
Regla de Oro
Las dependencias SIEMPRE apuntan hacia el centro:
interfaces → application → domain
infrastructure → domain
domain NO conoce capas externas
Tecnologías
Backend

Java 21 - Lenguaje de programación
Spring Boot 3.x - Framework principal
Spring Data JPA - Persistencia de datos
Spring Security - Seguridad y autenticación
Spring Data Redis - Cache y gestión de sesiones

Base de Datos

PostgreSQL - Base de datos principal (persistencia de reservas, usuarios, viajes)
MongoDB - Planificado para microservicios (tracking, logs)
Redis - Cache y gestión de retención de asientos

Migración de Datos

Flyway - Versionado y migración de esquemas de base de datos

Documentación API

SpringDoc OpenAPI 3 - Documentación automática de API REST (Swagger UI)

Seguridad

JWT (jjwt) - Tokens de autenticación (preparado para futuras implementaciones)
Spring Security - Control de acceso y seguridad

Testing

JUnit 5 - Framework de testing
Spring Boot Test - Testing de integración
H2 Database - Base de datos en memoria para tests

Build Tool

Gradle - Herramienta de construcción y gestión de dependencias

Requisitos Previos
Asegúrate de tener instalado:

Java 21 o superior (Descargar)
Gradle 8.x o superior (o usar el wrapper incluido ./gradlew)
PostgreSQL 14+ (Descargar)
Redis (Descargar)
Git (Descargar)

Instalación
1. Clonar el repositorio
bashgit clone https://github.com/tu-usuario/buses-venegas.git
cd buses-venegas
2. Configurar PostgreSQL
bash# Conectarse a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE buses_venegas_db;

# Crear usuario (opcional)
CREATE USER buses_admin WITH PASSWORD 'tu_password';
GRANT ALL PRIVILEGES ON DATABASE buses_venegas_db TO buses_admin;
3. Iniciar Redis
bash# En Linux/Mac
redis-server

# En Windows (con instalador)
redis-server.exe
Configuración
Variables de Entorno
Crea un archivo .env en la raíz del proyecto o configura las siguientes variables:
properties# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=buses_venegas_db
DB_USERNAME=postgres
DB_PASSWORD=tu_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Application
SERVER_PORT=8080
Archivo application.yml
El proyecto utiliza application.yml para la configuración. Asegúrate de ajustar estos valores:
yamlspring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:buses_venegas_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

server:
  port: ${SERVER_PORT:8080}
▶Ejecución
Desarrollo Local
bash# Opción 1: Usar el wrapper de Gradle (recomendado)
./gradlew bootRun  //ATENCION Revisar con marco

# Opción 2: Si tienes Gradle instalado globalmente
gradle bootRun

# Opción 3: Compilar y ejecutar el JAR
./gradlew clean build
java -jar build/libs/buses-venegas-api-0.0.1-SNAPSHOT.jar
La aplicación estará disponible en: http://localhost:8080
Verificar que está corriendo
bash# Health check
curl http://localhost:8080/actuator/health  //ATENCION Revisar con marco

# Respuesta esperada:
# {"status":"UP"}
Estructura del Proyecto
src/main/java/cl/venegas/buses_api/
│
├── BusesApiApplication.java                 # Clase principal
│
├── domain/                                   # CAPA 1: Núcleo del negocio
│   ├── model/
│   │   ├── entity/                          # Entidades de dominio
│   │   │   ├── Booking.java
│   │   │   ├── Bus.java
│   │   │   ├── Passenger.java
│   │   │   ├── Payment.java
│   │   │   ├── Route.java
│   │   │   ├── SeatHold.java
│   │   │   ├── Trip.java
│   │   │   └── User.java
│   │   └── valueobject/                     # Value Objects (inmutables)
│   │       ├── BookingStatus.java
│   │       ├── BusStatus.java
│   │       ├── BusType.java
│   │       ├── PaymentMethod.java
│   │       ├── PaymentStatus.java
│   │       └── UserRole.java
│   ├── repository/                          # Interfaces de repositorios
│   │   ├── BookingRepository.java
│   │   ├── BusRepository.java
│   │   ├── PaymentRepository.java
│   │   ├── RouteRepository.java
│   │   ├── SeatHoldRepository.java
│   │   ├── TripRepository.java
│   │   └── UserRepository.java
│   └── exception/                           # Excepciones de dominio
│       └── SeatAlreadyHeldException.java
│
├── application/                              # CAPA 2: Casos de uso
│   ├── usecase/
│   │   ├── booking/                         # Use cases de reservas
│   │   │   ├── CreateBookingUseCase.java
│   │   │   ├── CancelBookingUseCase.java
│   │   │   ├── ConfirmBookingUseCase.java
│   │   │   └── GetUserBookingsUseCase.java
│   │   ├── seat/                            # Use cases de asientos
│   │   │   └── HoldSeatsUseCase.java
│   │   ├── user/                            # Use cases de usuarios
│   │   │   └── RegisterUserUseCase.java
│   │   └── trip/                            # Use cases de viajes
│   │       └── SearchTripsUseCase.java
│   └── task/                                # Tareas programadas
│       └── ExpiredHoldsCleanupTask.java
│
├── infrastructure/                           # CAPA 3: Implementaciones técnicas
│   ├── config/                              # Configuraciones Spring
│   │   ├── CorsConfig.java
│   │   └── OpenApiConfig.java
│   ├── persistence/
│   │   └── jpa/
│   │       ├── adapter/                     # Adaptadores de repositorio
│   │       │   ├── BookingRepositoryJpa.java
│   │       │   ├── SeatHoldRepositoryJpa.java
│   │       │   ├── TripRepositoryJpa.java
│   │       │   └── UserRepositoryJpa.java
│   │       ├── entity/                      # Entidades JPA (anotadas)
│   │       │   ├── BookingJpa.java
│   │       │   ├── PaymentJpa.java
│   │       │   ├── SeatHoldJpa.java
│   │       │   ├── TripJpa.java
│   │       │   └── UserJpa.java
│   │       └── repository/                  # Spring Data repositories
│   │           ├── BookingJpaRepository.java
│   │           ├── SeatHoldJpaRepository.java
│   │           ├── TripJpaRepository.java
│   │           └── UserJpaRepository.java
│   └── security/                            # Configuración de seguridad
│       └── SecurityConfig.java
│
└── interfaces/                               # CAPA 4: Adaptadores externos
    └── web/                                 # Adaptador REST
        ├── controller/                      # Controladores REST
        │   ├── BookingsController.java
        │   ├── HoldsController.java
        │   ├── SeatHoldController.java
        │   ├── TripsController.java
        │   └── UsersController.java
        ├── dto/
        │   ├── request/                     # DTOs de entrada
        │   │   ├── CreateBookingRequest.java
        │   │   ├── HoldRequest.java
        │   │   ├── PassengerRequest.java
        │   │   └── RegisterUserRequest.java
        │   └── response/                    # DTOs de salida
        │       ├── BookingResponse.java
        │       ├── TripResponse.java
        │       └── UserResponse.java
        ├── exception/                       # Manejo de excepciones HTTP
        │   └── RestExceptionHandler.java
        └── mapper/                          # Mappers DTO ↔ Domain
            ├── BookingDTOMapper.java
            ├── TripDTOMapper.java
            ├── UserDTOMapper.java
            └── SeatHoldDTOMapper.java


Principios de la Arquitectura
Domain Layer (Núcleo)

NO tiene dependencias externas
Contiene la lógica de negocio pura
Define interfaces (repositories) que serán implementadas por capas externas

Application Layer

Orquesta casos de uso
Depende solo del dominio
Cada Use Case representa una operación de negocio específica

Infrastructure Layer

Implementa las interfaces del dominio
Maneja detalles técnicos (base de datos, cache, etc.)
Puede ser reemplazada sin afectar el dominio

Interfaces Layer

Expone la funcionalidad a través de REST API
Convierte entre DTOs y objetos del dominio
Delega la lógica a los Use Cases

📚 API Documentation
Swagger UI
Una vez que la aplicación esté corriendo, accede a la documentación interactiva:
http://localhost:8080/swagger-ui.html
Endpoints Principales
Reservas (Bookings)
httpPOST   /api/bookings          # Crear nueva reserva
GET    /api/bookings/{id}     # Obtener reserva por ID
GET    /api/bookings/user/{userId}  # Obtener reservas de un usuario
PUT    /api/bookings/{id}/confirm   # Confirmar reserva
DELETE /api/bookings/{id}     # Cancelar reserva
Viajes (Trips)
httpGET    /api/trips             # Buscar viajes disponibles
GET    /api/trips/{id}        # Obtener detalles de un viaje
Usuarios (Users)
httpPOST   /api/users             # Registrar nuevo usuario
GET    /api/users/{id}        # Obtener usuario por ID
Retención de Asientos (Seat Holds)
httpPOST   /api/holds             # Retener asientos temporalmente
DELETE /api/holds/{id}        # Liberar asientos retenidos
Ejemplos de Request/Response
Crear Reserva
Request:
httpPOST /api/bookings
Content-Type: application/json

{
  "tripId": "trip-123",
  "userId": "user-456",
  "seatNumber": "A12"
}
Response:
json{
  "id": "booking-789",
  "tripId": "trip-123",
  "userId": "user-456",
  "seatNumber": "A12",
  "price": 15000,
  "status": "PENDING",
  "createdAt": "2025-11-18T10:30:00"
}
🧪 Testing
Ejecutar Tests
bash# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte de cobertura
open build/reports/jacoco/test/html/index.html
Estructura de Tests
src/test/java/cl/venegas/buses_api/
├── domain/
│   ├── model/
│   │   └── BookingTest.java
│   └── service/
│       └── PricingServiceTest.java
├── application/
│   └── usecase/
│       └── booking/
│           └── CreateBookingUseCaseTest.java
└── interfaces/
    └── web/
        └── controller/
            └── BookingsControllerTest.java
Validación de Arquitectura
El proyecto incluye un script para validar que las dependencias respetan las reglas de Onion Architecture:
bash# Ejecutar validación
./scripts/validate-onion-dependencies.sh
🗺️ Roadmap
Fase 1: Sistema Base ✅ (Completado)

 Gestión de reservas
 Sistema de retención de asientos
 Búsqueda de viajes
 Gestión de usuarios
 Migración a Onion Architecture

Fase 2: Microservicios 🚧 (En Desarrollo)

 Microservicio de tracking de buses (MongoDB)

Geolocalización en tiempo real
Historial de rutas
Estado de flota


 Microservicio de notificaciones

Envío de pasajes por WhatsApp
Envío de pasajes por correo electrónico
Notificaciones de cambios y retrasos



Fase 3: Mejoras Futuras 📋 (Planificado)

 Dashboard administrativo
 Sistema de reportes y analíticas
 App móvil (React Native)
 Sistema de puntos de fidelidad
 Integración con múltiples pasarelas de pago

👥 Equipo
Desarrolladores

Marcos Venegas - GitHub
Jorge Pfeifer - GitHub

Proyecto Académico
Este proyecto es desarrollado como parte de un proyecto académico enfocado en arquitecturas limpias y buenas prácticas de desarrollo de software.

Licencia:
Este proyecto es privado y pertenece a Buses Venegas S.A.

Contribuir
Flujo de Trabajo

Crear una rama desde develop:

bash   git checkout develop
   git pull origin develop
   git checkout -b feature/nueva-funcionalidad

Hacer commits descriptivos:

bash   git commit -m "feat(booking): add payment validation"

Push y crear Pull Request:

bash   git push origin feature/nueva-funcionalidad

Esperar code review y merge a develop


Desarrollado por el equipo de Buses Venegas S.A.
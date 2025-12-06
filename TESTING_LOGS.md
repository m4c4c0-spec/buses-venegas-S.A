# Guía de Prueba: Sistema de Logs

## Paso 1: Ejecutar el Backend

### Opción A: Usando Gradle Wrapper (Recomendado)

```powershell
# Navegar al directorio del backend
cd c:\Users\jorge\Desktop\buses_venegas_S.A\backend\buses-api

# Ejecutar el backend
.\gradlew.bat bootRun
```

### Opción B: Usando Maven Wrapper

```powershell
cd c:\Users\jorge\Desktop\buses_venegas_S.A\backend\buses-api
.\mvnw.cmd spring-boot:run
```

**Qué esperar:**
- El backend iniciará en `http://localhost:8080`
- Verás logs en la consola
- Se creará automáticamente el archivo `logs/application.log`

---

## Paso 2: Verificar que el Archivo de Log se Creó

```powershell
# Verificar que existe el archivo
ls logs\application.log

# Ver contenido del archivo
cat logs\application.log

# O ver las últimas líneas en tiempo real
Get-Content logs\application.log -Wait -Tail 20
```

**Qué esperar:**
- Archivo `logs/application.log` existe
- Contiene logs del inicio de Spring Boot
- Formato: `yyyy-MM-dd HH:mm:ss.SSS NIVEL [Clase] - Mensaje`

---

## Paso 3: Probar Endpoints con curl

### 3.1 Buscar Viajes (GET)

```powershell
curl http://localhost:8080/api/v1/trips/search?origin=Santiago&destination=Concepción&date=2025-12-05
```

**Logs esperados en `application.log`:**
```
2025-12-04 18:05:00.001 INFO  [TripsController] - Recibido GET /api/v1/trips/search: origin=Santiago, destination=Concepción
```

### 3.2 Confirmar Reserva (PUT)

```powershell
curl -X PUT http://localhost:8080/api/v1/bookings/1/confirm
```

**Logs esperados:**
```
2025-12-04 18:05:10.001 INFO  [BookingsController] - Recibido PUT /api/v1/bookings/1/confirm: bookingId=1
2025-12-04 18:05:10.123 INFO  [ConfirmBookingService] - Iniciando confirmación de reserva: bookingId=1
2025-12-04 18:05:10.234 DEBUG [ConfirmBookingService] - Reserva encontrada: bookingId=1, status=PENDIENTE
2025-12-04 18:05:10.345 DEBUG [ConfirmBookingService] - Validando estado de reserva: bookingId=1, status=PENDIENTE
2025-12-04 18:05:10.456 DEBUG [ConfirmBookingService] - Validando expiración: bookingId=1, expiresAt=...
2025-12-04 18:05:10.567 INFO  [ConfirmBookingService] - Reserva confirmada exitosamente: bookingId=1, newStatus=CONFIRMADO
2025-12-04 18:05:10.678 INFO  [BookingsController] - Reserva confirmada exitosamente: bookingId=1, status=CONFIRMADO
```

### 3.3 Cancelar Reserva (DELETE)

```powershell
curl -X DELETE http://localhost:8080/api/v1/bookings/1
```

**Logs esperados:**
```
2025-12-04 18:05:20.001 INFO  [BookingsController] - Recibido DELETE /api/v1/bookings/1: bookingId=1
2025-12-04 18:05:20.123 INFO  [CancelBookingService] - Iniciando cancelación de reserva: bookingId=1
2025-12-04 18:05:20.234 DEBUG [CancelBookingService] - Reserva encontrada: bookingId=1, status=CONFIRMADO
2025-12-04 18:05:20.345 DEBUG [CancelBookingService] - Liberando asientos retenidos: tripId=..., seats=[...]
2025-12-04 18:05:20.456 INFO  [CancelBookingService] - Reserva cancelada exitosamente: bookingId=1
2025-12-04 18:05:20.567 INFO  [BookingsController] - Reserva cancelada exitosamente: bookingId=1
```

### 3.4 Obtener Reservas de Usuario (GET)

```powershell
curl http://localhost:8080/api/v1/bookings/user/123
```

**Logs esperados:**
```
2025-12-04 18:05:30.001 INFO  [BookingsController] - Recibido GET /api/v1/bookings/user/123: userId=123
2025-12-04 18:05:30.123 INFO  [BookingsController] - Retornando 3 reservas para userId=123
```

---

## Paso 4: Ver Logs en Tiempo Real

### Opción 1: PowerShell

```powershell
# Ver archivo completo
cat logs\application.log

# Ver últimas 50 líneas
Get-Content logs\application.log -Tail 50

# Ver en tiempo real (como tail -f)
Get-Content logs\application.log -Wait -Tail 20
```

### Opción 2: Notepad

```powershell
notepad logs\application.log
```

### Opción 3: VS Code

```powershell
code logs\application.log
```

---

## Paso 5: Probar con Frontend

Si tienes el frontend corriendo (`npm run dev`):

1. Abrir `http://localhost:5173`
2. Ir a "Confirma tu pasaje"
3. Ingresar código de reserva: `1`
4. Click en "CONFIRMAR PASAJE"
5. Ver logs generados en `logs/application.log`

---

## Paso 6: Verificar Formato de Logs

**Formato esperado:**
```
yyyy-MM-dd HH:mm:ss.SSS NIVEL [NombreClase] - Mensaje
```

**Ejemplo real:**
```
2025-12-04 18:05:10.123 INFO  [BookingsController] - Recibido PUT /api/v1/bookings/1/confirm: bookingId=1
```

**Componentes:**
- ✅ Timestamp: `2025-12-04 18:05:10.123`
- ✅ Nivel: `INFO`
- ✅ Clase: `[BookingsController]`
- ✅ Mensaje: `Recibido PUT /api/v1/bookings/1/confirm: bookingId=1`

---

## Comandos Rápidos de Referencia

```powershell
# Ejecutar backend
cd backend\buses-api
.\gradlew.bat bootRun

# Ver logs en tiempo real (en otra terminal)
cd backend\buses-api
Get-Content logs\application.log -Wait -Tail 20

# Probar endpoint
curl -X PUT http://localhost:8080/api/v1/bookings/1/confirm

# Ver archivo completo
cat logs\application.log
```

---

## Troubleshooting

### Problema: El archivo `logs/application.log` no se crea

**Solución:**
1. Verificar que `logback-spring.xml` existe en `src/main/resources/`
2. Verificar que la carpeta `logs/` existe
3. Reiniciar el backend

### Problema: No aparecen logs de DEBUG

**Solución:**
Los logs DEBUG solo aparecen en el archivo, no en consola. Revisar `logs/application.log`.

### Problema: Backend no inicia

**Solución:**
1. Verificar que PostgreSQL está corriendo
2. Verificar que la base de datos `buses` existe
3. Ver errores en consola

---

## Preparación para la Demo

### Fragmento de Código a Mostrar

**Archivo:** `BookingsController.java`

```java
private static final Logger log = LoggerFactory.getLogger(BookingsController.class);

@PutMapping("/{bookingId}/confirm")
public ResponseEntity<BookingResponse> confirmBooking(@PathVariable String bookingId) {
    log.info("Recibido PUT /api/v1/bookings/{}/confirm: bookingId={}", bookingId, bookingId);
    
    try {
        Booking booking = confirmBookingUseCase.execute(bookingId);
        BookingResponse response = mapper.toResponse(booking);
        
        log.info("Reserva confirmada exitosamente: bookingId={}, status={}",
                response.id(), response.status());
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("Error al confirmar reserva: bookingId={}", bookingId, e);
        throw e;
    }
}
```

### Archivo de Log a Mostrar

**Archivo:** `logs/application.log`

Mostrar un flujo completo de confirmación de reserva.

### Flujo End-to-End

1. Ejecutar: `curl -X PUT http://localhost:8080/api/v1/bookings/1/confirm`
2. Mostrar logs generados en `application.log`
3. Explicar cada línea del log

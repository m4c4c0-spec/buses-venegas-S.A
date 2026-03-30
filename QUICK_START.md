# Guía Rápida: Ejecutar Aplicación Completa

## Prerequisitos

- ✅ Node.js instalado
- ✅ Java 17+ instalado
- ⏳ PostgreSQL instalado (ver `POSTGRESQL_SETUP.md`)
- ⏳ Base de datos `buses` creada

---

## Paso 1: Verificar PostgreSQL

```powershell
# Verificar que PostgreSQL está corriendo
Get-Service -Name postgresql*

# Si no está corriendo, iniciarlo
Start-Service -Name postgresql-x64-16

# Verificar conexión
psql -U buses -d buses -h localhost
# Password: buses
# Si conecta exitosamente, escribir \q para salir
```

---

## Paso 2: Ejecutar Backend

```powershell
# Navegar al directorio del backend
cd backend\buses-api

# Opción 1: Usando Gradle Wrapper (recomendado)
.\gradlew.bat bootRun

# Opción 2: Usando Maven Wrapper
.\mvnw.cmd spring-boot:run

# El backend debería iniciar en: http://localhost:8080
# Flyway aplicará las migraciones automáticamente
```

**Verificar que el backend está corriendo:**
- Buscar en los logs: "Started BusesApiApplication"
- Verificar endpoint: http://localhost:8080/api/v1/trips/search?origin=Santiago&destination=Concepción&date=2025-12-05

---

## Paso 3: Ejecutar Frontend

**En una nueva terminal:**

```powershell
# Navegar al directorio del frontend
cd frontend

# Instalar dependencias (si no se hizo antes)
npm install

# Ejecutar en modo desarrollo
npm run dev

# El frontend debería iniciar en: http://localhost:5173
```

---

## Paso 4: Probar la Aplicación

1. **Abrir navegador:** http://localhost:5173

2. **Probar Búsqueda de Viajes:**
   - Ir a "Compra tu pasaje"
   - Ingresar:
     - Origen: Santiago
     - Destino: Concepción
     - Fecha: (cualquier fecha futura)
   - Click en "BUSCAR PASAJES"
   - Deberían aparecer viajes disponibles

3. **Probar Confirmación:**
   - Ir a "Confirma tu pasaje"
   - Ingresar código de reserva (ej: 1)
   - Click en "CONFIRMAR PASAJE"

4. **Probar Cancelación:**
   - Ir a "Anula tu pasaje"
   - Ingresar código de reserva
   - Aceptar políticas
   - Click en "ANULAR PASAJE"

---

## Solución de Problemas

### Backend no inicia

**Error: "Connection refused" o "Unable to connect to database"**

```powershell
# Verificar que PostgreSQL está corriendo
Get-Service -Name postgresql*

# Verificar que la base de datos existe
psql -U postgres -c "\l" | findstr buses

# Si no existe, crearla
psql -U postgres -f setup_database.sql
```

**Error: "Flyway migration failed"**

```powershell
# Limpiar migraciones y volver a intentar
psql -U postgres -d buses -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
psql -U postgres -d buses -c "GRANT ALL ON SCHEMA public TO buses;"

# Reiniciar backend
```

### Frontend no se conecta con Backend

**Error: "Network Error" o "CORS"**

1. Verificar que el backend está corriendo en http://localhost:8080
2. Verificar archivo `.env` en frontend:
   ```
   VITE_API_BASE_URL=http://localhost:8080/api/v1
   ```
3. Reiniciar frontend: `npm run dev`

### No aparecen datos

**Verificar que hay datos en la base de datos:**

```powershell
psql -U buses -d buses -h localhost

# En psql:
SELECT COUNT(*) FROM trips;
SELECT * FROM trips LIMIT 3;

# Si no hay datos, verificar que las migraciones se ejecutaron
SELECT * FROM flyway_schema_history;
```

---

## Comandos Útiles

### Backend

```powershell
# Ver logs en tiempo real
.\gradlew.bat bootRun

# Compilar sin ejecutar
.\gradlew.bat build

# Limpiar y compilar
.\gradlew.bat clean build

# Ejecutar tests
.\gradlew.bat test
```

### Frontend

```powershell
# Modo desarrollo
npm run dev

# Build para producción
npm run build

# Preview del build
npm run preview

# Limpiar node_modules
rm -r node_modules
npm install
```

### PostgreSQL

```powershell
# Conectar a base de datos
psql -U buses -d buses

# Ver tablas
\dt

# Ver datos de viajes
SELECT * FROM trips;

# Ver reservas
SELECT * FROM bookings;

# Salir
\q
```

---

## Estructura de Puertos

- **Frontend:** http://localhost:5173
- **Backend:** http://localhost:8080
- **PostgreSQL:** localhost:5432
- **pgAdmin:** http://localhost:5050 (si se instaló)

---

## Próximos Pasos

Una vez que todo esté funcionando:

1. ✅ Probar todos los flujos de la aplicación
2. ✅ Verificar que los datos se guardan en PostgreSQL
3. ✅ Revisar logs del backend para errores
4. ✅ Probar con diferentes navegadores
5. ⏳ Implementar mejoras (toast notifications, validación, etc.)

---

## Detener la Aplicación

```powershell
# Frontend: Ctrl + C en la terminal

# Backend: Ctrl + C en la terminal

# PostgreSQL (si quieres detenerlo):
Stop-Service -Name postgresql-x64-16
```

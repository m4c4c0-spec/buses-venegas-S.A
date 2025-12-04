# Guía de Instalación y Configuración de PostgreSQL

## 1. Instalación de PostgreSQL en Windows

### Opción 1: Instalador Oficial (Recomendado)

1. **Descargar PostgreSQL:**
   - Ir a: https://www.postgresql.org/download/windows/
   - Descargar el instalador de PostgreSQL 16 (o la versión más reciente)
   - Ejecutar el instalador

2. **Durante la Instalación:**
   - **Password del superusuario (postgres):** Anotar la contraseña que elijas
   - **Puerto:** Dejar 5432 (por defecto)
   - **Locale:** Spanish, Chile (o dejar por defecto)
   - **Componentes:** Instalar todos (PostgreSQL Server, pgAdmin 4, Command Line Tools)

3. **Verificar Instalación:**
   ```powershell
   # Agregar PostgreSQL al PATH (si no se agregó automáticamente)
   # Buscar la carpeta de instalación, típicamente:
   # C:\Program Files\PostgreSQL\16\bin
   
   # Verificar versión
   psql --version
   ```

### Opción 2: Usando Docker (Alternativa)

```powershell
# Instalar Docker Desktop para Windows primero
# Luego ejecutar:
docker run --name postgres-buses -e POSTGRES_PASSWORD=buses -e POSTGRES_USER=buses -e POSTGRES_DB=buses -p 5432:5432 -d postgres:16

# Verificar que está corriendo
docker ps
```

---

## 2. Crear Base de Datos y Usuario

### Método 1: Usando pgAdmin 4 (GUI)

1. Abrir pgAdmin 4
2. Conectar al servidor local (localhost)
3. Click derecho en "Databases" → "Create" → "Database"
4. Nombre: `buses`
5. Owner: postgres
6. Save

7. Click derecho en "Login/Group Roles" → "Create" → "Login/Group Role"
8. Name: `buses`
9. Password: `buses`
10. Privileges: Can login
11. Save

12. Click derecho en la base de datos `buses` → "Properties" → "Security"
13. Agregar privilegios al usuario `buses`

### Método 2: Usando psql (Línea de Comandos)

```powershell
# Conectar como superusuario
psql -U postgres

# En el prompt de psql:
CREATE DATABASE buses;
CREATE USER buses WITH PASSWORD 'buses';
GRANT ALL PRIVILEGES ON DATABASE buses TO buses;

# Conectar a la base de datos buses
\c buses

# Dar permisos al usuario buses
GRANT ALL ON SCHEMA public TO buses;

# Salir
\q
```

### Método 3: Usando Script SQL (Automático)

Ejecutar el archivo `setup_database.sql` que se creará a continuación:

```powershell
psql -U postgres -f setup_database.sql
```

---

## 3. Verificar Conexión

```powershell
# Conectar a la base de datos buses
psql -U buses -d buses -h localhost

# Si pide contraseña, ingresar: buses

# Verificar que estás conectado
\conninfo

# Listar tablas (debería estar vacío por ahora)
\dt

# Salir
\q
```

---

## 4. Ejecutar el Backend

Una vez que PostgreSQL esté instalado y la base de datos creada:

```powershell
# Navegar al directorio del backend
cd backend\buses-api

# Ejecutar con Gradle (Windows)
.\gradlew.bat bootRun

# O con Maven (si usas Maven)
.\mvnw.cmd spring-boot:run
```

**Flyway aplicará las migraciones automáticamente** al iniciar el backend.

---

## 5. Verificar Migraciones

Después de ejecutar el backend, verificar que las tablas se crearon:

```powershell
# Conectar a la base de datos
psql -U buses -d buses -h localhost

# Listar tablas
\dt

# Deberías ver tablas como:
# - trips
# - bookings
# - users
# - seat_holds
# - flyway_schema_history

# Ver datos de ejemplo
SELECT * FROM trips LIMIT 5;

# Salir
\q
```

---

## 6. Solución de Problemas

### Error: "psql: command not found"

**Solución:** Agregar PostgreSQL al PATH

1. Buscar la carpeta de instalación: `C:\Program Files\PostgreSQL\16\bin`
2. Agregar al PATH del sistema:
   - Windows + R → `sysdm.cpl`
   - Pestaña "Opciones avanzadas"
   - "Variables de entorno"
   - En "Variables del sistema", editar "Path"
   - Agregar: `C:\Program Files\PostgreSQL\16\bin`
   - Reiniciar PowerShell

### Error: "Connection refused"

**Solución:** Verificar que PostgreSQL esté corriendo

```powershell
# Verificar servicio de PostgreSQL
Get-Service -Name postgresql*

# Si no está corriendo, iniciarlo
Start-Service -Name postgresql-x64-16
```

### Error: "password authentication failed"

**Solución:** Verificar usuario y contraseña

- Usuario: `buses`
- Contraseña: `buses`
- Base de datos: `buses`

Si olvidaste la contraseña del superusuario `postgres`, puedes restablecerla editando `pg_hba.conf`.

### Error: Flyway migrations fail

**Solución:** Verificar que el usuario `buses` tenga permisos

```sql
-- Conectar como postgres
psql -U postgres -d buses

-- Dar todos los permisos
GRANT ALL PRIVILEGES ON DATABASE buses TO buses;
GRANT ALL ON SCHEMA public TO buses;
GRANT ALL ON ALL TABLES IN SCHEMA public TO buses;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO buses;
```

---

## 7. Configuración Alternativa (Cambiar Credenciales)

Si quieres usar credenciales diferentes, editar `backend/buses-api/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/buses
    username: TU_USUARIO
    password: TU_PASSWORD
```

---

## 8. Próximos Pasos

1. ✅ Instalar PostgreSQL
2. ✅ Crear base de datos `buses`
3. ✅ Crear usuario `buses`
4. ✅ Ejecutar backend (Flyway aplicará migraciones)
5. ✅ Verificar que las tablas se crearon
6. ✅ Ejecutar frontend (`npm run dev`)
7. ✅ Probar integración completa

---

## 9. Comandos Útiles de PostgreSQL

```powershell
# Conectar a base de datos
psql -U buses -d buses -h localhost

# Listar bases de datos
\l

# Listar tablas
\dt

# Describir tabla
\d nombre_tabla

# Ver datos
SELECT * FROM trips;

# Salir
\q
```

---

## 10. Recursos

- **Documentación oficial:** https://www.postgresql.org/docs/
- **pgAdmin 4:** Interfaz gráfica incluida con PostgreSQL
- **DBeaver:** Alternativa gratuita de GUI: https://dbeaver.io/

-- Script de Configuración de Base de Datos para Buses Venegas S.A.
-- Ejecutar como superusuario (postgres)

-- Crear base de datos
CREATE DATABASE buses
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Chile.1252'
    LC_CTYPE = 'Spanish_Chile.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE buses IS 'Base de datos para sistema de reservas de buses';

-- Crear usuario
CREATE USER buses WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    CONNECTION LIMIT -1
    PASSWORD 'buses';

-- Conectar a la base de datos buses
\c buses

-- Dar permisos al usuario buses
GRANT ALL PRIVILEGES ON DATABASE buses TO buses;
GRANT ALL ON SCHEMA public TO buses;
GRANT ALL ON ALL TABLES IN SCHEMA public TO buses;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO buses;
GRANT ALL ON ALL FUNCTIONS IN SCHEMA public TO buses;

-- Permisos futuros (para tablas que se crearán)
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO buses;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO buses;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO buses;

-- Verificación
SELECT 'Base de datos creada exitosamente' AS status;
SELECT current_database() AS database_name;
SELECT current_user AS current_user;

-- Listar usuarios
\du

-- Listar bases de datos
\l

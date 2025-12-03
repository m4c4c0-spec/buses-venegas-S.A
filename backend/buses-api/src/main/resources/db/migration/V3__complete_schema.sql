-- Migración V3: Schema Completo
-- Crea tablas faltantes: users, bookings, payments
-- Actualiza tabla seat_hold

-- ============================================
-- Tabla de Usuarios
-- ============================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT chk_role CHECK (role IN ('CLIENTE', 'ADMIN'))
);

-- ============================================
-- Tabla de Reservas
-- ============================================

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    trip_id BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
    seats TEXT[] NOT NULL,
    passengers JSONB NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(10,2),
    payment_reference VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDIENTE', 'CONFIRMADO', 'CANCELADO', 'EXPIRADO'))
);

-- ============================================
-- Tabla de Pagos
-- ============================================

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDIENTE', 'APROBADO', 'RECHAZADO', 'REEMBOLSADO')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- ============================================
-- Actualizar tabla seat_hold (agregar referencia a usuario)
-- ============================================

ALTER TABLE seat_hold 
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

ALTER TABLE seat_hold 
    ADD CONSTRAINT fk_seat_hold_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- ============================================
-- Índices para Rendimiento
-- ============================================

-- Índices en bookings
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_trip_id ON bookings(trip_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created_at ON bookings(created_at);
CREATE INDEX idx_bookings_expires_at ON bookings(expires_at);

-- Índices en payments
CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);

-- Índices en users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Índices en seat_hold (adicionales)
CREATE INDEX idx_seat_hold_user_id ON seat_hold(user_id);

-- ============================================
-- Comentarios de Documentación
-- ============================================

COMMENT ON TABLE users IS 'Almacena información de usuarios del sistema';
COMMENT ON TABLE bookings IS 'Reservas de pasajes realizadas por usuarios';
COMMENT ON TABLE payments IS 'Pagos asociados a reservas';
COMMENT ON COLUMN bookings.passengers IS 'JSON con información de pasajeros: [{firstName, lastName, documentType, documentNumber, email, phone}]';
COMMENT ON COLUMN bookings.seats IS 'Array de números de asiento reservados (ej: {A1, A2, B3})';

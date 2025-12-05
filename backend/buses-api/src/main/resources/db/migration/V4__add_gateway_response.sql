-- Migración V4: Agregar columna gateway_response a payments
-- Requerida por la entidad PaymentJpa

ALTER TABLE payments 
    ADD COLUMN IF NOT EXISTS gateway_response TEXT;

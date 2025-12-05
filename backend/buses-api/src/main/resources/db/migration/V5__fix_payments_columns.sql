-- Migración V5: Corregir nombres de columnas en payments
-- La entidad JPA espera 'method' pero V3 creó 'payment_method'

ALTER TABLE payments 
    RENAME COLUMN payment_method TO method;

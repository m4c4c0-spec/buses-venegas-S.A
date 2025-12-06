-- Add varied trips across multiple routes and times
-- Santiago - Concepción (Dec 5-10)
INSERT INTO trip(origin, destination, departure_ts, base_price_clp)
VALUES
  ('Santiago', 'Concepción', '2025-12-05 06:30:00', 12500),
  ('Santiago', 'Concepción', '2025-12-05 09:00:00', 15000),
  ('Santiago', 'Concepción', '2025-12-05 13:30:00', 16500),
  ('Santiago', 'Concepción', '2025-12-05 18:00:00', 14000),
  ('Santiago', 'Concepción', '2025-12-05 22:30:00', 11000),
  ('Santiago', 'Concepción', '2025-12-06 07:00:00', 13000),
  ('Santiago', 'Concepción', '2025-12-06 14:00:00', 15500),
  ('Santiago', 'Concepción', '2025-12-06 20:00:00', 12000),
  ('Santiago', 'Concepción', '2025-12-07 08:30:00', 14500),
  ('Santiago', 'Concepción', '2025-12-07 16:00:00', 16000),
  ('Santiago', 'Concepción', '2025-12-08 10:00:00', 15000),
  ('Santiago', 'Concepción', '2025-12-09 12:00:00', 14000),
  ('Santiago', 'Concepción', '2025-12-10 15:00:00', 13500),

-- Concepción - Santiago (Dec 5-10)
  ('Concepción', 'Santiago', '2025-12-05 07:00:00', 12500),
  ('Concepción', 'Santiago', '2025-12-05 11:00:00', 15000),
  ('Concepción', 'Santiago', '2025-12-05 15:30:00', 16000),
  ('Concepción', 'Santiago', '2025-12-05 19:00:00', 13500),
  ('Concepción', 'Santiago', '2025-12-05 23:00:00', 10500),
  ('Concepción', 'Santiago', '2025-12-06 08:00:00', 13000),
  ('Concepción', 'Santiago', '2025-12-07 09:30:00', 14500),
  ('Concepción', 'Santiago', '2025-12-08 11:00:00', 15000),

-- Santiago - Temuco (Dec 5-8)
  ('Santiago', 'Temuco', '2025-12-05 08:00:00', 18000),
  ('Santiago', 'Temuco', '2025-12-05 14:00:00', 19500),
  ('Santiago', 'Temuco', '2025-12-05 20:00:00', 17000),
  ('Santiago', 'Temuco', '2025-12-06 09:00:00', 18500),
  ('Santiago', 'Temuco', '2025-12-07 10:00:00', 19000),
  ('Santiago', 'Temuco', '2025-12-08 13:00:00', 18000),

-- Temuco - Santiago (Dec 5-8)
  ('Temuco', 'Santiago', '2025-12-05 07:30:00', 18000),
  ('Temuco', 'Santiago', '2025-12-05 13:30:00', 19000),
  ('Temuco', 'Santiago', '2025-12-06 10:00:00', 18500),
  ('Temuco', 'Santiago', '2025-12-07 11:30:00', 19500),

-- Santiago - Valparaíso (Dec 5-7)
  ('Santiago', 'Valparaíso', '2025-12-05 06:00:00', 5500),
  ('Santiago', 'Valparaíso', '2025-12-05 09:00:00', 6000),
  ('Santiago', 'Valparaíso', '2025-12-05 12:00:00', 6500),
  ('Santiago', 'Valparaíso', '2025-12-05 15:00:00', 6000),
  ('Santiago', 'Valparaíso', '2025-12-05 18:00:00', 5500),
  ('Santiago', 'Valparaíso', '2025-12-06 07:00:00', 5800),
  ('Santiago', 'Valparaíso', '2025-12-06 14:00:00', 6200),
  ('Santiago', 'Valparaíso', '2025-12-07 10:00:00', 6000),

-- Valparaíso - Santiago (Dec 5-7)
  ('Valparaíso', 'Santiago', '2025-12-05 08:00:00', 5500),
  ('Valparaíso', 'Santiago', '2025-12-05 11:00:00', 6000),
  ('Valparaíso', 'Santiago', '2025-12-05 14:00:00', 6500),
  ('Valparaíso', 'Santiago', '2025-12-05 17:00:00', 6000),
  ('Valparaíso', 'Santiago', '2025-12-05 20:00:00', 5500),
  ('Valparaíso', 'Santiago', '2025-12-06 09:00:00', 5800),
  ('Valparaíso', 'Santiago', '2025-12-07 12:00:00', 6000);

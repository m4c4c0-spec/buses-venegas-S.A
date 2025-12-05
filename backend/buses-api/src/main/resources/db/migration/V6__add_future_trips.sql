-- Add more trips with future dates for testing
INSERT INTO trip(origin, destination, departure_date, price)
VALUES
  ('Santiago', 'Concepción', '2025-12-05', 15000),
  ('Santiago', 'Concepción', '2025-12-05', 18000),
  ('Santiago', 'Concepción', '2025-12-05', 12000),
  ('Concepción', 'Santiago', '2025-12-06', 15000),
  ('Santiago', 'Temuco', '2025-12-07', 20000),
  ('Temuco', 'Santiago', '2025-12-08', 20000),
  ('Santiago', 'Valparaíso', '2025-12-05', 8000),
  ('Valparaíso', 'Santiago', '2025-12-05', 8000);

CREATE TABLE trip (
  id BIGSERIAL PRIMARY KEY,
  origin        VARCHAR(80) NOT NULL,
  destination   VARCHAR(80) NOT NULL,
  departure_date DATE       NOT NULL,
  price         NUMERIC(10,2) NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE seat_hold (
  id BIGSERIAL PRIMARY KEY,
  trip_id     BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
  seat_number VARCHAR(8) NOT NULL,
  status      VARCHAR(16) NOT NULL,      -- HELD | CONFIRMED | RELEASED
  hold_until  TIMESTAMP   NOT NULL,
  created_at  TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_hold_trip_seat ON seat_hold(trip_id, seat_number);
CREATE INDEX ix_hold_status_until ON seat_hold(status, hold_until);

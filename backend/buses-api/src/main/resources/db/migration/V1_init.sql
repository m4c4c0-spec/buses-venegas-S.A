@"
create table if not exists trip(
  id bigserial primary key,
  origin varchar(80) not null,
  destination varchar(80) not null,
  departure_ts timestamp not null,
  arrival_ts timestamp not null,
  base_price_clp integer not null
);

create table if not exists seat_hold(
  id bigserial primary key,
  trip_id bigint not null references trip(id),
  seat varchar(8) not null,
  user_id bigint,
  expires_at timestamp not null,
  unique(trip_id, seat)
);

create index if not exists idx_trip_od_dep on trip(origin, destination, departure_ts);
"@ | Set-Content -Encoding UTF8 src\main\resources\db\migration\V1__init.sql
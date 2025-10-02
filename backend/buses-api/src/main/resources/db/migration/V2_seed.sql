@"
insert into trip(origin,destination,departure_ts,arrival_ts,base_price_clp) values
('Temuco','Santiago','2025-10-02 18:30:00','2025-10-03 06:00:00',12990)
on conflict do nothing;
"@ | Set-Content -Encoding UTF8 src\main\resources\db\migration\V2__seed.sql
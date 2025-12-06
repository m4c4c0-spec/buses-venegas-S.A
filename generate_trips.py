import datetime

cities = [
    "Arica", "Iquique", "Antofagasta", "Calama", "Copiapó", "La Serena", "Coquimbo",
    "Valparaíso", "Viña del Mar", "Santiago", "Rancagua", "Talca", "Chillán",
    "Concepción", "Talcahuano", "Los Ángeles", "Temuco", "Valdivia", "Osorno",
    "Puerto Montt", "Puerto Varas", "Punta Arenas"
]

# Hub-and-spoke model: Santiago connects to everyone
# Plus some regional connections
routes = []
for city in cities:
    if city != "Santiago":
        routes.append(("Santiago", city))
        routes.append((city, "Santiago"))

# Additional regional routes
regional_routes = [
    ("Valparaíso", "Viña del Mar"), ("Viña del Mar", "Valparaíso"),
    ("Concepción", "Talcahuano"), ("Talcahuano", "Concepción"),
    ("Concepción", "Temuco"), ("Temuco", "Concepción"),
    ("Temuco", "Valdivia"), ("Valdivia", "Temuco"),
    ("Puerto Montt", "Puerto Varas"), ("Puerto Varas", "Puerto Montt"),
    ("La Serena", "Coquimbo"), ("Coquimbo", "La Serena")
]
routes.extend(regional_routes)

start_date = datetime.date(2025, 12, 5)
days_to_generate = 7
hours = range(7, 20) # 7 to 19 (7 PM)

sql_statements = ["-- Comprehensive list of trips for Chilean cities (7 AM - 7 PM)",
                  "INSERT INTO trip(origin, destination, departure_ts, base_price_clp) VALUES"]

values = []

for day_offset in range(days_to_generate):
    current_date = start_date + datetime.timedelta(days=day_offset)
    for origin, destination in routes:
        # Base price calculation (rough approximation based on distance/random)
        # Just hardcoding tiers for simplicity
        price = 15000
        if origin in ["Valparaíso", "Viña del Mar", "Rancagua"] and destination == "Santiago": price = 5000
        if destination in ["Valparaíso", "Viña del Mar", "Rancagua"] and origin == "Santiago": price = 5000
        if origin in ["Arica", "Iquique", "Punta Arenas"] or destination in ["Arica", "Iquique", "Punta Arenas"]: price = 45000
        
        for hour in hours:
            # Format: 'YYYY-MM-DD HH:MM:00'
            departure_time = f"{current_date.strftime('%Y-%m-%d')} {hour:02d}:00:00"
            values.append(f"  ('{origin}', '{destination}', '{departure_time}', {price})")

# Join values with commas
sql_statements.append(",\n".join(values) + ";")

with open('backend/buses-api/src/main/resources/db/migration/V8__add_comprehensive_chile_trips.sql', 'w', encoding='utf-8') as f:
    f.write("\n".join(sql_statements))

print(f"Generated {len(values)} trips.")

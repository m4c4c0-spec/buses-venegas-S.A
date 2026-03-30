import datetime

cities = ["Victoria"]

# Routes for Victoria (connecting to Santiago and Temuco)
routes = [
    ("Santiago", "Victoria"), ("Victoria", "Santiago"),
    ("Temuco", "Victoria"), ("Victoria", "Temuco")
]

start_date = datetime.date(2025, 12, 5)
days_to_generate = 7
hours = range(7, 20) # 7 to 19 (7 PM)

sql_statements = ["-- Add trips for Victoria (7 AM - 7 PM)",
                  "INSERT INTO trip(origin, destination, departure_ts, base_price_clp) VALUES"]

values = []

for day_offset in range(days_to_generate):
    current_date = start_date + datetime.timedelta(days=day_offset)
    for origin, destination in routes:
        price = 12000 # Base price for Victoria
        if origin == "Temuco" or destination == "Temuco": price = 4000
        
        for hour in hours:
            # Format: 'YYYY-MM-DD HH:MM:00'
            departure_time = f"{current_date.strftime('%Y-%m-%d')} {hour:02d}:00:00"
            values.append(f"  ('{origin}', '{destination}', '{departure_time}', {price})")

# Join values with commas
sql_statements.append(",\n".join(values) + ";")

with open('backend/buses-api/src/main/resources/db/migration/V9__add_victoria_trips.sql', 'w', encoding='utf-8') as f:
    f.write("\n".join(sql_statements))

print(f"Generated {len(values)} trips for Victoria.")

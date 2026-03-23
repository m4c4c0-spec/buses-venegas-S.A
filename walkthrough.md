# Rastreo de Buses en Tiempo Real (Mapa de Ciudades)

¡Hemos completado la implementación de la función de seguimiento interactiva solicitada!

## 1. Integración en el Flujo Principal
En lugar de crear una pantalla totalmente separada, hemos mejorado la vista nativa de **Seguimiento** que ya estaba esbozada en la aplicación.
Ahora, cuando el usuario hace clic en `Seguimiento` e ingresa su `Código de Reserva` y su `Correo de Compra`, el sistema hace una llamado directo al Backend para extraer los horarios precisos.

> [!NOTE]
> Modificamos el campo "RUT" por "Correo del Comprador" en esta vista, puesto que el Backend verifica la propiedad del pasaje utilizando el Correo Electrónico brindado al momento de la compra para proteger la privacidad del usuario.

## 2. Lógica de Simulación de Tiempo y Mapa
Dado que la plataforma funcionará con estimaciones de horario, hemos construido un sistema dinámico:
* Al ingresar el código, el sistema detecta el **Origen** y **Destino** (Ej: Santiago -> Concepción) e inyecta las ciudades intermedias conocidas (Rancagua, Talca, Chillán).
* Utilizando la **Hora Actual del Sistema** comparada la hora de salida y llegada del boleto, se dibuja una barra de progreso.
* Un **Autobús Animado** se mueve proporcionalmente sobre la ruta completando las paradas una a una.
* Una caja azul prominente muestra el **Tiempo Faltante** de forma exacta en formato de minutos y la ciudad donde *debería* encontrarse el bus en ese instante.

## 3. Pruebas y Resultados
Utilizamos pruebas de navegador exhaustivas comprando boletos generados dinámicamente (`BB...`) procesados a través de la simulación segura de Mercado Pago. Todo viaja y se conecta perfectamente.

### Demostración del Mapa de Rastreo
![Tracking Map UI](file:///C:/Users/Acer/.gemini/antigravity/brain/ff40f6ae-0aa2-45ac-acd1-420fc01dcc02/tracking_map_ui_1774288185261.png)

### Video del Flujo Completo (Compra + Rastreo)
A continuación, puedes observar una grabación que demuestra cómo se realiza un pago desde cero mediante la interfaz que hemos creado juntos, para luego pasar rápidamente a rastrear dicho pasaje en tiempo real. 

![Flujo Compra y Rastreo](file:///C:/Users/Acer/.gemini/antigravity/brain/ff40f6ae-0aa2-45ac-acd1-420fc01dcc02/rastreo_mapa_test_final_1774287885062.webp)

# 🚌 Buses Venegas S.A. - Sistema de Reservas

Sistema completo de reserva y venta de pasajes de buses, desde la búsqueda de viajes hasta el pago y emisión de boletos digitales.

Proyecto académico en desarrollo, construido con una arquitectura Cliente-Servidor desplegada en la nube.

---

## ✨ Funcionalidades

- **Reserva de pasajes:** Búsqueda de viajes por origen/destino/fecha, selección interactiva de asientos y formulario con validación de RUT chileno.
- **Pagos con Mercado Pago:** Integración con el SDK de Mercado Pago (Sandbox / Checkout Bricks).
- **Modo demostración:** Botón de bypass para simular compras completas sin necesidad de tarjeta de crédito.
- **Procesamiento asíncrono:** Las operaciones pesadas (BD y correo) se ejecutan en segundo plano con `CompletableFuture` para evitar bloqueos en la UI.
- **Correo de confirmación:** Envío automático del comprobante al email del pasajero vía Gmail SMTPS (puerto 465).
- **Rastreo de buses:** Mapa interactivo con Leaflet.js para seguimiento de la flota en tiempo real.

---

## 🛠️ Tecnologías

### Frontend
| Tecnología | Uso |
|---|---|
| Vue.js 3 | Framework principal |
| Vite | Bundler y dev server |
| CSS3 | Estilos, animaciones y diseño responsive |
| Leaflet.js | Mapas OpenStreetMap |
| @mercadopago/sdk-js | Pasarela de pagos |

### Backend
| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot 3.x | Framework del servidor |
| PostgreSQL | Base de datos relacional |
| Spring Data JPA / Hibernate | ORM |
| Bucket4j | Rate limiting |

### Despliegue
| Servicio | Componente |
|---|---|
| [Vercel](https://buses-venegas-s-a-zcb1.vercel.app/) | Frontend |
| [Render](https://buses-venegas-backend.onrender.com) | Backend (Docker) |

> **Nota:** Render en plan gratuito puede tardar ~50s en responder tras periodos de inactividad (cold start). El frontend muestra indicadores de carga en estos casos.

---

## 💻 Instalación local

### Requisitos previos
- Node.js v20+
- JDK 21
- PostgreSQL
- Gradle

### 1. Clonar el repositorio
```bash
git clone https://github.com/m4c4c0-spec/buses-venegas-S.A.git
cd buses-venegas-S.A
```

### 2. Backend
Configurar las variables de entorno:
- `SPRING_DATASOURCE_URL` → ej: `jdbc:postgresql://localhost:5432/buses_db`
- `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`
- `MAIL_USERNAME` / `MAIL_PASSWORD` → usar [Contraseña de Aplicación](https://myaccount.google.com/apppasswords) de Google

```bash
cd backend/buses-api
./gradlew bootRun
```
El servidor queda disponible en `http://localhost:8081`.

### 3. Frontend
Crear archivo `.env.development`:
```env
VITE_API_BASE_URL=http://localhost:8081
VITE_MERCADOPAGO_PUBLIC_KEY=TEST-tu-llave-publica
```

```bash
cd frontend
npm install
npm run dev
```

---

## 📝 Notas técnicas
- Se usa Vite en lugar de Vue CLI por tiempos de build más rápidos.
- El envío de correos usa el puerto 465 (SMTPS/SSL) en vez del 587 (STARTTLS) para compatibilidad con servidores cloud que bloquean puertos SMTP tradicionales.
- El backend procesa la persistencia y el envío de emails de forma asíncrona para no bloquear la respuesta HTTP al cliente.

## 🎓 Autor
Proyecto académico en desarrollo continuo.
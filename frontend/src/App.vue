<template>
  <div id="app">
    <header class="header">
      <div class="logo-container">
        <img src="/src/assets/logo.png" alt="Logo Buses Bio Bío" class="logo" />
      </div>
      <nav class="nav-links">
        <a href="#" @click.prevent="showQuienesSomos = true">¿QUIÉNES SOMOS?</a>
        <a href="#" @click.prevent="showInscribete = true">INSCRÍBETE CON NOSOTROS</a>
        <a href="about:blank" target="_blank">AYUDA <i class="fas fa-chevron-down"></i></a>
        <a href="about:blank" target="_blank"><i class="fas fa-user-circle"></i></a>
      </nav>
    </header>

    <!-- Popups -->
    <div v-if="showQuienesSomos" class="popup-overlay" @click.self="showQuienesSomos = false">
      <div class="popup-content">
        <h2>¿Quiénes Somos?</h2>
        <p>Somos Buses Venegas S.A., una empresa sólida y comprometida con acercar a nuestra gente y unir las distintas regiones de Chile. Llevamos años entregando un servicio de transporte terrestre caracterizado por la comodidad, puntualidad y seguridad total hacia nuestros pasajeros.</p>
        <button class="btn-cerrar" @click="showQuienesSomos = false">Cerrar</button>
      </div>
    </div>

    <div v-if="showInscribete" class="popup-overlay" @click.self="showInscribete = false">
      <div class="popup-content">
        <i class="fas fa-tools icon-mantenimiento"></i>
        <h2>Inscríbete con Nosotros</h2>
        <p>Este sitio se encuentra actualmente en mantenimiento temporal. ¡Vuelve pronto para descubrir nuestras nuevas ofertas!</p>
        <button class="btn-cerrar" @click="showInscribete = false">Entendido</button>
      </div>
    </div>

    <div class="header-banner">
      <div class="header-overlay">
        <h1 class="main-title">Acercamos a nuestra gente, uniendo regiones</h1>
        <p class="subtitle">Viaja cómodo y seguro por todo Chile</p>
      </div>
    </div>

    <div class="servicios-nav">
      <button
          class="servicio-btn"
          :class="{ active: seccionActiva === 'compra' }"
          @click="seccionActiva = 'compra'"
      >
        <i class="fas fa-ticket-alt"></i>
        <span>Compra tu pasaje</span>
      </button>
      <button
          class="servicio-btn"
          :class="{ active: seccionActiva === 'cambia' }"
          @click="seccionActiva = 'cambia'"
      >
        <i class="fas fa-exchange-alt"></i>
        <span>Cambia tu pasaje</span>
      </button>
      <button
          class="servicio-btn"
          :class="{ active: seccionActiva === 'anula' }"
          @click="seccionActiva = 'anula'"
      >
        <i class="fas fa-times-circle"></i>
        <span>Anula tu pasaje</span>
      </button>
      <button
          class="servicio-btn"
          :class="{ active: seccionActiva === 'seguimiento' }"
          @click="seccionActiva = 'seguimiento'"
      >
        <i class="fas fa-search"></i>
        <span>Seguimiento</span>
      </button>
    </div>

    <BuscadorBoletos v-if="seccionActiva === 'compra'" @iniciar-flujo="irAHorarios" />
    <SeleccionHorario v-else-if="seccionActiva === 'horarios'" :detallesReserva="detallesReserva" @horario-seleccionado="irAAsientos" @volver="seccionActiva = 'compra'" />
    <SeleccionAsientos v-else-if="seccionActiva === 'asientos'" :detallesReserva="detallesReserva" @asientos-seleccionados="irADatosPasajero" @volver="seccionActiva = 'horarios'" />
    <DatosPasajero v-else-if="seccionActiva === 'datos_pasajero'" :detallesReserva="detallesReserva" @datos-ingresados="irSiguientePaso" @volver="seccionActiva = 'asientos'" />
    
    <!-- Vuelta steps (reuse same components with esVuelta flag) -->
    <SeleccionHorario v-else-if="seccionActiva === 'horarios_vuelta'" :detallesReserva="detallesReserva" :esVuelta="true" @horario-seleccionado="irAAsientosVuelta" @volver="seccionActiva = 'datos_pasajero'" />
    <SeleccionAsientos v-else-if="seccionActiva === 'asientos_vuelta'" :detallesReserva="detallesReserva" @asientos-seleccionados="irADatosPasajeroVuelta" @volver="seccionActiva = 'horarios_vuelta'" />
    
    <PagoPasaje v-else-if="seccionActiva === 'pago'" :detallesReserva="detallesReserva" @pago-exitoso="irAExito" @volver="volverDesdePago" />
    <div v-else-if="seccionActiva === 'pago_cargando'" class="loading-full">
      <div class="spinner"></div>
      <h2>Confirmando tu pago...</h2>
    </div>
    <ComprobanteExito v-else-if="seccionActiva === 'exito'" :detallesReserva="detallesReserva" @volver-inicio="volverAlInicio" />

    <CambiaPasaje v-if="seccionActiva === 'cambia'" @iniciar-cambio="iniciarCambioDeViaje" />
    <AnulaPasaje v-if="seccionActiva === 'anula'" />
    <SeguimientoPasaje v-if="seccionActiva === 'seguimiento'" />

    <section class="info-section">
      <div class="info-container">
        <div class="info-card">
          <i class="fas fa-shield-alt info-icon"></i>
          <h3>Viaja Seguro</h3>
          <p>Cumplimos con todos los protocolos de seguridad para tu tranquilidad</p>
        </div>
        <div class="info-card">
          <i class="fas fa-clock info-icon"></i>
          <h3>Puntualidad</h3>
          <p>Salidas y llegadas a tiempo, respetando tu horario</p>
        </div>
        <div class="info-card">
          <i class="fas fa-wifi info-icon"></i>
          <h3>Conectividad</h3>
          <p>Wi-Fi gratuito en todos nuestros buses</p>
        </div>
        <div class="info-card">
          <i class="fas fa-star info-icon"></i>
          <h3>Comodidad</h3>
          <p>Asientos reclinables y espaciosos para tu comodidad</p>
        </div>
      </div>
    </section>

    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>Buses Venegas S.A</h4>
          <p>Conectando a nuestra gente desde 1995</p>
        </div>
        <div class="footer-section">
          <h4>Contacto</h4>
          <p><i class="fas fa-phone"></i> +56 9 8765 4321</p>
          <p><i class="fas fa-envelope"></i> contacto@busesvenegas.cl</p>
        </div>
        <div class="footer-section">
          <h4>Síguenos</h4>
          <div class="social-links">
            <a href="about:blank"><i class="fab fa-facebook"></i></a>
            <a href="about:blank"><i class="fab fa-instagram"></i></a>
            <a href="about:blank"><i class="fab fa-twitter"></i></a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© {{ currentYear }} Buses Venegas S.A. Todos los derechos reservados.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import BuscadorBoletos from "./components/BuscadorBoletos.vue";
import CambiaPasaje from "./components/CambiarPasaje.vue";
import AnulaPasaje from "./components/AnularPasaje.vue";
import SeguimientoPasaje from "./components/SeguimientoPasaje.vue";
import PagoPasaje from "./components/PagoPasaje.vue";
import SeleccionHorario from "./components/SeleccionHorario.vue";
import SeleccionAsientos from "./components/SeleccionAsientos.vue";
import DatosPasajero from "./components/DatosPasajero.vue";
import ComprobanteExito from "./components/ComprobanteExito.vue";

export default {
  name: "App",
  components: {
    BuscadorBoletos,
    CambiaPasaje,
    AnulaPasaje,
    SeguimientoPasaje,
    SeleccionHorario,
    PagoPasaje,
    SeleccionAsientos,
    DatosPasajero,
    ComprobanteExito,
  },
  data() {
    return {
      currentYear: new Date().getFullYear(),
      seccionActiva: "compra",
      detallesReserva: null,
      showQuienesSomos: false,
      showInscribete: false,
    };
  },
  mounted() {
    // Intercepción de redirección de Mercado Pago (por ej. Webpay 3DS)
    const params = new URLSearchParams(window.location.search);
    const paymentId = params.get('payment_id');
    const status = params.get('status');

    if (paymentId && status === 'approved') {
      const storedReserva = localStorage.getItem('reservaPendiente');
      if (storedReserva) {
        this.detallesReserva = JSON.parse(storedReserva);
        this.seccionActiva = 'pago_cargando';
        
        // Notificar al backend de la compra confirmada (envía el correo y registra)
        fetch('http://localhost:8081/api/payments/confirm', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            detalles: this.detallesReserva,
            paymentId: paymentId
          })
        })
        .then(res => res.json())
        .then(data => {
            if (data.idReserva) {
                this.detallesReserva.idReserva = data.idReserva;
            }
            this.seccionActiva = 'exito';
            window.history.replaceState({}, document.title, window.location.pathname);
            localStorage.removeItem('reservaPendiente');
        })
        .catch(err => {
            console.error("Error confirmando envío de correo de boleta:", err);
            this.seccionActiva = 'exito';
            window.history.replaceState({}, document.title, window.location.pathname);
            localStorage.removeItem('reservaPendiente');
        });
      }
    }
  },
  methods: {
    irAHorarios(detalles) {
      this.detallesReserva = detalles;
      this.seccionActiva = 'horarios';
    },
    irAAsientos(horario) {
      if (this.detallesReserva.modoCambio) {
        // Ejecutar cambio de pasaje directamente sin pedir asientos ni datos
        fetch(`http://localhost:8081/api/reservas/${this.detallesReserva.idReservaOriginal}/cambiar`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            nuevaFecha: this.detallesReserva.fechaIda, // Asumimos misma fecha por ahora o la seleccionada
            nuevoHorarioSalida: horario.salida,
            nuevoHorarioLlegada: horario.llegada
          })
        }).then(res => {
          if(res.ok) {
            alert('¡Tu pasaje ha sido cambiado con éxito!');
            this.volverAlInicio();
          } else {
            alert('Hubo un error al cambiar el pasaje.');
          }
        }).catch(err => console.error(err));
        return;
      }
      
      this.detallesReserva.horarioViaje = horario;
      this.seccionActiva = 'asientos';
    },
    irADatosPasajero(asientosSeleccionados) {
      this.detallesReserva.asientos = asientosSeleccionados;
      this.seccionActiva = 'datos_pasajero';
    },
    irSiguientePaso(datosPasajeros) {
      this.detallesReserva.pasajerosData = datosPasajeros;
      // If round-trip, go to return trip flow
      if (this.detallesReserva.idaYVuelta) {
        this.seccionActiva = 'horarios_vuelta';
      } else {
        this.seccionActiva = 'pago';
      }
    },
    irAAsientosVuelta(horario) {
      if (!this.detallesReserva.vuelta) {
        this.detallesReserva.vuelta = {};
      }
      this.detallesReserva.vuelta.horarioViaje = horario;
      this.seccionActiva = 'asientos_vuelta';
    },
    irADatosPasajeroVuelta(asientosSeleccionados) {
      this.detallesReserva.vuelta.asientos = asientosSeleccionados;
      // For return trip, reuse the same passenger data (no need to re-enter)
      this.seccionActiva = 'pago';
    },
    volverDesdePago() {
      if (this.detallesReserva.idaYVuelta) {
        this.seccionActiva = 'asientos_vuelta';
      } else {
        this.seccionActiva = 'datos_pasajero';
      }
    },
    irAExito() {
      this.seccionActiva = 'exito';
    },
    volverAlInicio() {
      this.detallesReserva = null;
      this.seccionActiva = 'compra';
    },
    iniciarCambioDeViaje(reserva) {
      if (!reserva) return;
      this.detallesReserva = {
        modoCambio: true,
        idReservaOriginal: reserva.id,
        origen: reserva.origen,
        destino: reserva.destino,
        fechaIda: reserva.fechaViaje,
        horarioOriginal: reserva.horarioSalida, 
        pasajerosData: JSON.parse(reserva.pasajerosJson || '[]'),
        emailContacto: reserva.emailContacto,
        idaYVuelta: false
      };
      this.seccionActiva = 'horarios';
    }
  }
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: "Poppins", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f5f5;
}

.popup-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.popup-content {
  background: white;
  padding: 40px;
  border-radius: 12px;
  max-width: 450px;
  text-align: center;
  box-shadow: 0 15px 30px rgba(0,0,0,0.2);
  animation: scaleIn 0.3s ease-out;
}

.popup-content h2 {
  color: #0d286d;
  margin-bottom: 15px;
}

.popup-content p {
  color: #444;
  line-height: 1.6;
  margin-bottom: 25px;
}

.icon-mantenimiento {
  font-size: 3rem;
  color: #ffc107;
  margin-bottom: 15px;
}

.btn-cerrar {
  background-color: #0d286d;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s;
}

.btn-cerrar:hover {
  background-color: #174291;
}

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 50px;
  background-color: #0d286d;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  height: 50px;
  transition: transform 0.3s;
}

.logo:hover {
  transform: scale(1.05);
}

.nav-links a {
  margin-left: 20px;
  text-decoration: none;
  color: white;
  font-weight: 500;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  transition: opacity 0.3s;
}

.nav-links a:hover {
  opacity: 0.8;
}

.header-banner {
  background-image: url("https://i.imgur.com/K1LgO3u.jpeg");
  background-size: cover;
  background-position: center;
  height: 500px;
  position: relative;
}

.header-overlay {
  background: linear-gradient(135deg, rgba(13, 40, 109, 0.95) 0%, rgba(23, 66, 145, 0.85) 100%);
  width: 50%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 10%;
}

.main-title {
  color: white;
  font-size: 3rem;
  line-height: 1.2;
  margin-bottom: 15px;
  animation: fadeInUp 1s ease-out;
}

.subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.3rem;
  font-weight: 300;
  animation: fadeInUp 1s ease-out 0.2s backwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.servicios-nav {
  display: flex;
  justify-content: center;
  background-color: #174291;
  width: 90%;
  max-width: 1200px;
  margin: -50px auto 20px auto;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  z-index: 10;
  position: relative;
  overflow: hidden;
}

.servicio-btn {
  background-color: transparent;
  border: none;
  color: white;
  padding: 20px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  opacity: 0.7;
  transition: all 0.3s ease;
  flex: 1;
  position: relative;
}

.servicio-btn::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #ffeb3b;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.servicio-btn:hover {
  opacity: 0.9;
  background-color: rgba(13, 40, 109, 0.3);
}

.servicio-btn.active {
  background-color: #0d286d;
  opacity: 1;
}

.servicio-btn.active::after {
  transform: scaleX(1);
}

.servicio-btn i {
  font-size: 1.8rem;
  margin-bottom: 8px;
}

.servicio-btn span {
  font-size: 0.85rem;
  font-weight: 500;
  text-align: center;
}

.info-section {
  padding: 60px 20px;
  background-color: white;
}

.info-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.info-card {
  text-align: center;
  padding: 30px 20px;
  background-color: #f9f9f9;
  border-radius: 12px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.info-icon {
  font-size: 3rem;
  color: #0d286d;
  margin-bottom: 15px;
}

.info-card h3 {
  color: #0d286d;
  margin-bottom: 10px;
  font-size: 1.3rem;
}

.info-card p {
  color: #666;
  line-height: 1.6;
}

.footer {
  background-color: #1a1a1a;
  color: white;
  padding: 40px 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  padding-bottom: 30px;
}

.footer-section h4 {
  color: #ffeb3b;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.footer-section p {
  color: #ccc;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.social-links {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.social-links a {
  color: white;
  font-size: 1.5rem;
  transition: color 0.3s;
}

.social-links a:hover {
  color: #ffeb3b;
}

.footer-bottom {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid #333;
  color: #888;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    padding: 15px 20px;
  }

  .nav-links {
    margin-top: 15px;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }

  .nav-links a {
    margin-left: 0;
    font-size: 0.8rem;
  }

  .header-overlay {
    width: 100%;
    padding: 20px;
  }

  .main-title {
    font-size: 2rem;
  }

  .subtitle {
    font-size: 1rem;
  }

  .servicios-nav {
    flex-wrap: wrap;
    margin: -30px auto 20px auto;
  }

  .servicio-btn {
    padding: 15px 10px;
  }

  .servicio-btn span {
    font-size: 0.75rem;
  }
}
</style>


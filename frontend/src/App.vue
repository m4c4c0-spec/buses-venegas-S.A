<template>
  <div id="app">
    <header class="header">
      <div class="logo-container">
        <img src="/src/assets/logo.png" alt="Logo Buses Bio Bío" class="logo" />
      </div>
      <nav class="nav-links">
        <a href="about:blank" target="_blank">¡PREPARA TU VIAJE! <i class="fas fa-chevron-down"></i></a>
        <a href="about:blank" target="_blank">AHORRA <i class="fas fa-chevron-down"></i></a>
        <a href="about:blank" target="_blank">SERVICIOS ADICIONALES <i class="fas fa-chevron-down"></i></a>
        <a href="about:blank" target="_blank">CLUB MUVIY <i class="fas fa-chevron-down"></i></a>
        <a href="about:blank" target="_blank">AYUDA <i class="fas fa-chevron-down"></i></a>
        <a href="about:blank" target="_blank"><i class="fas fa-user-circle"></i></a>
      </nav>
    </header>

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
          :class="{ active: seccionActiva === 'confirma' }"
          @click="seccionActiva = 'confirma'"
      >
        <i class="fas fa-check-circle"></i>
        <span>Confirma tu pasaje</span>
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

    <BuscadorBoletos v-if="seccionActiva === 'compra'" />
    <CambiaPasaje v-if="seccionActiva === 'cambia'" />
    <ConfirmaPasaje v-if="seccionActiva === 'confirma'" />
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
          <h4>Buses Bio Bío</h4>
          <p>Conectando Chile desde 1980</p>
        </div>
        <div class="footer-section">
          <h4>Contacto</h4>
          <p><i class="fas fa-phone"></i> +56 9 1234 5678</p>
          <p><i class="fas fa-envelope"></i> contacto@busesbiobio.cl</p>
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
        <p>© {{ currentYear }} Buses Bio Bío. Todos los derechos reservados.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import "./assets/App.css";
import BuscadorBoletos from "./components/BuscadorBoletos.vue";
import CambiaPasaje from "./components/CambiarPasaje.vue";
import ConfirmaPasaje from "./components/ConfirmarPasaje.vue";
import AnulaPasaje from "./components/AnularPasaje.vue";
import SeguimientoPasaje from "./components/SeguimientoPasaje.vue";

export default {
  name: "App",
  components: {
    BuscadorBoletos,
    CambiaPasaje,
    ConfirmaPasaje,
    AnulaPasaje,
    SeguimientoPasaje,
  },
  data() {
    return {
      currentYear: new Date().getFullYear(),
      seccionActiva: "compra",
    };
  },
};
</script>

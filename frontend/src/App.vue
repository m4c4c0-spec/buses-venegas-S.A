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


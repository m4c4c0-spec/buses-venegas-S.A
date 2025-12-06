<template>
  <div id="app">
    <header class="header">
      <div class="logo-container" @click="activeModal = null" style="cursor: pointer;">
        <img src="/src/assets/logo_buses_venegas_sa.jpg" alt="Logo Buses Venegas" class="logo" />
      </div>
      <nav class="nav-links">
        <a href="#" @click.prevent="activeModal = 'prepara'">¡PREPARA TU VIAJE! <i class="fas fa-chevron-down"></i></a>
        <a href="#" @click.prevent="activeModal = 'ahorra'">AHORRA <i class="fas fa-chevron-down"></i></a>
        <a href="#" @click.prevent="activeModal = 'servicios'">SERVICIOS <i class="fas fa-chevron-down"></i></a>
        <a href="#" @click.prevent="activeModal = 'ayuda'">AYUDA <i class="fas fa-chevron-down"></i></a>
        <a href="#" @click.prevent="activeModal = 'login'"><i class="fas fa-user-circle"></i></a>
      </nav>
    </header>

    <!-- Main Content Background (Always Visible) -->
    <div class="header-banner" @click="activeModal = 'banner'" style="cursor: pointer;">
      <div class="header-overlay">
        <h1 class="main-title">Acercamos a nuestra gente, uniendo regiones</h1>
        <p class="subtitle">Viaja cómodo y seguro por todo Chile</p>
      </div>
    </div>

    <div class="servicios-nav">
      <button
          class="servicio-btn"
          :class="{ active: activeModal === 'compra' }"
          @click="activeModal = 'compra'"
      >
        <i class="fas fa-ticket-alt"></i>
        <span>Compra tu pasaje</span>
      </button>
      <button
          class="servicio-btn"
          :class="{ active: activeModal === 'cambia' }"
          @click="activeModal = 'cambia'"
      >
        <i class="fas fa-exchange-alt"></i>
        <span>Cambia tu pasaje</span>
      </button>
      <button
          class="servicio-btn"
          :class="{ active: activeModal === 'confirma' }"
          @click="activeModal = 'confirma'"
      >
        <i class="fas fa-check-circle"></i>
        <span>Confirma tu pasaje</span>
      </button>
    </div>

    <!-- Main Content Background (Always Visible) -->
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

    <!-- Modals -->
    <BaseModal 
      :is-open="activeModal === 'banner'" 
      title="Nuestra Filosofía" 
      @close="activeModal = null"
    >
      <div style="padding: 20px; color: white;">
        <h3>Acercamos a nuestra gente, uniendo regiones</h3>
        <p>En Buses Venegas S.A., nos dedicamos a conectar cada rincón de Chile con seguridad y confort.</p>
        <p>Desde 1980 brindando el mejor servicio a nuestros pasajeros.</p>
        <ul style="margin-top: 15px; padding-left: 20px;">
            <li>Flota moderna y segura</li>
            <li>Conductores profesionales</li>
            <li>Puntualidad garantizada</li>
        </ul>
      </div>
    </BaseModal>

    <!-- Header Link Modals -->
    <BaseModal 
      :is-open="activeModal === 'prepara'" 
      title="Prepara tu Viaje" 
      @close="activeModal = null"
    >
      <div style="padding: 20px; color: white;">
        <h3>Todo lo que necesitas saber</h3>
        <p>Información sobre equipaje, documentos necesarios y recomendaciones para tu viaje.</p>
        <ul>
          <li>Llega 30 minutos antes.</li>
          <li>Lleva tu cédula de identidad.</li>
          <li>Equipaje permitido: hasta 30kg en bodega.</li>
        </ul>
      </div>
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'ahorra'" 
      title="Club de Ahorro" 
      @close="activeModal = null"
    >
      <div style="padding: 20px; color: white;">
        <h3>¡Únete y Ahorra!</h3>
        <p>Acumula puntos en cada viaje y canjéalos por pasajes gratis.</p>
        <button class="btn-primary" style="margin-top: 10px;">Inscribirse</button>
      </div>
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'servicios'" 
      title="Nuestros Servicios" 
      @close="activeModal = null"
    >
      <div style="padding: 40px; text-align: center; color: #64748b;">
        <i class="fas fa-tools" style="font-size: 3rem; margin-bottom: 20px; color: #3b82f6;"></i>
        <h3 style="color: #1e293b; margin-bottom: 10px;">En Mantenimiento</h3>
        <p>Estamos trabajando para mejorar esta sección. Vuelve pronto.</p>
      </div>
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'ayuda'" 
      title="Centro de Ayuda" 
      @close="activeModal = null"
    >
      <div style="padding: 20px; color: white;">
        <h3>¿Necesitas ayuda?</h3>
        <p>Contáctanos a través de nuestros canales oficiales.</p>
        <p><i class="fas fa-phone"></i> +56 9 1234 5678</p>
        <p><i class="fas fa-envelope"></i> ayuda@busesvenegas.cl</p>
      </div>
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'login'" 
      title="Acceso Usuarios" 
      @close="activeModal = null"
    >
      <div style="padding: 40px; text-align: center; color: #64748b;">
        <i class="fas fa-user-lock" style="font-size: 3rem; margin-bottom: 20px; color: #3b82f6;"></i>
        <h3 style="color: #1e293b; margin-bottom: 10px;">En Mantenimiento</h3>
        <p>El acceso a usuarios está temporalmente deshabilitado.</p>
      </div>
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'compra'" 
      title="Comprar Pasaje" 
      @close="activeModal = null"
    >
      <BuscadorBoletos />
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'cambia'" 
      title="Cambiar Pasaje" 
      @close="activeModal = null"
    >
      <CambiaPasaje />
    </BaseModal>

    <BaseModal 
      :is-open="activeModal === 'confirma'" 
      title="Confirmar Pasaje" 
      @close="activeModal = null"
    >
      <ConfirmaPasaje />
    </BaseModal>

    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>Buses Venegas S.A.</h4>
          <p>Conectando Chile desde 1980</p>
        </div>
        <div class="footer-section">
          <h4>Contacto</h4>
          <p><i class="fas fa-phone"></i> +56 9 1234 5678</p>
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
import "./assets/App.css";
import BuscadorBoletos from "./components/BuscadorBoletos.vue";
import CambiaPasaje from "./components/CambiarPasaje.vue";
import ConfirmaPasaje from "./components/ConfirmarPasaje.vue";
import BaseModal from "./components/common/BaseModal.vue";

export default {
  name: "App",
  components: {
    BuscadorBoletos,
    CambiaPasaje,
    ConfirmaPasaje,
    BaseModal,
  },
  data() {
    return {
      currentYear: new Date().getFullYear(),
      activeModal: null, // 'compra', 'cambia', 'confirma', 'prepara', 'ahorra', 'servicios', 'ayuda', 'login', 'banner' or null
    };
  },
};
</script>

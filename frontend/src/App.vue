<template>
  <div id="app">
    <!-- HEADER -->
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

    <!-- BANNER PRINCIPAL -->
    <div class="header-banner">
      <div class="header-overlay">
        <h1 class="main-title">Acercamos a nuestra gente, uniendo regiones</h1>
      </div>
    </div>

    <!-- FRANJA DE OPCIONES -->
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
          :class="{ active: seccionActiva === 'cuponera' }"
          @click="seccionActiva = 'cuponera'"
      >
        <i class="fas fa-percent"></i>
        <span>Seguimiento</span>
      </button>
    </div>

    <!-- ⬇️ BUSCADOR SOLO CUANDO ES 'COMPRA' -->
    <BuscadorBoletos v-if="seccionActiva === 'compra'" />

    <!-- CONTENIDO DE OTRAS SECCIONES -->
    <main>
      <div v-if="seccionActiva === 'cambia'" class="seccion-vacia">
        <h2>Sección de Cambio de Pasajes</h2>
        <p>Aquí iría el formulario para cambiar un pasaje.</p>
      </div>
      <div v-if="seccionActiva === 'confirma'" class="seccion-vacia">
        <h2>Sección de Confirmación de Pasajes</h2>
        <p>Aquí iría el formulario para confirmar un pasaje.</p>
      </div>
      <div v-if="seccionActiva === 'anula'" class="seccion-vacia">
        <h2>Sección de Anulación de Pasajes</h2>
        <p>Aquí iría el formulario para anular un pasaje.</p>
      </div>
      <div v-if="seccionActiva === 'cuponera'" class="seccion-vacia">
        <h2>Seguimiento de Pasajes</h2>
        <p>Aquí iría la información de seguimiento.</p>
      </div>
    </main>

    <!-- FOOTER -->
    <footer class="footer">
      <p>© {{ currentYear }} Buses Bio Bío. Todos los derechos reservados.</p>
    </footer>
  </div>
</template>

<script>
import BuscadorBoletos from "./components/BuscadorBoletos.vue";

export default {
  name: "App",
  components: {
    BuscadorBoletos,
  },
  data() {
    return {
      currentYear: new Date().getFullYear(),
      seccionActiva: "compra", // 'compra', 'cambia', 'confirma', etc.
    };
  },
};
</script>

<style>
body {
  margin: 0;
  font-family: "Poppins", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 50px;
  background-color: #0d286d;
}

.logo {
  height: 50px;
}

.nav-links a {
  margin-left: 20px;
  text-decoration: none;
  color: white;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

/* Banner */
.header-banner {
  background-image: url("https://i.imgur.com/K1LgO3u.jpeg");
  background-size: cover;
  background-position: center;
  height: 500px;
  position: relative;
}

.header-overlay {
  background-color: #0d286d;
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  padding-left: 10%;
}

.main-title {
  color: white;
  font-size: 3rem;
  line-height: 1.2;
}

/* Franja de opciones */
.servicios-nav {
  display: flex;
  justify-content: center;
  background-color: #174291;
  width: 90%;
  max-width: 1200px;
  margin: -50px auto 20px auto; /* sigue montándose un poco sobre el banner */
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  z-index: 10;
  position: relative;
}

.servicio-btn {
  background-color: transparent;
  border: none;
  color: white;
  padding: 15px 25px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.servicio-btn.active {
  background-color: #0d286d;
  opacity: 1;
}

.servicio-btn i {
  font-size: 1.5rem;
  margin-bottom: 5px;
}

/* Secciones vacías */
.seccion-vacia {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 40vh;
  text-align: center;
  color: #333;
}

/* Footer */
.footer {
  text-align: center;
  padding: 20px;
  background-color: #333;
  color: white;
}
</style>


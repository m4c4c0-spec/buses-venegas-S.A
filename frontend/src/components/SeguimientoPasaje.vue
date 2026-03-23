<template>
  <div class="form-card">
    <div class="form-header">
      <i class="fas fa-search"></i>
      <h2>Seguimiento de Pasaje</h2>
      <p>Rastrea el estado de tu reserva</p>
    </div>

    <form @submit.prevent="buscarPasaje">
      <div class="form-row">
        <div class="form-group">
          <label for="codigoReserva">
            <i class="fas fa-ticket-alt"></i> Código de Reserva
          </label>
          <input
              type="text"
              id="codigoReserva"
              placeholder="Ejemplo: BB123456789"
              v-model="form.codigoReserva"
              required
          />
        </div>

        <div class="form-group">
          <label for="rut">
            <i class="fas fa-envelope"></i> Correo de Compra
          </label>
          <input
              type="text"
              id="rut"
              placeholder="ejemplo@correo.com"
              v-model="form.rut"
              required
          />
        </div>
      </div>

      <button type="submit" class="btn-submit">
        <i class="fas fa-search"></i> BUSCAR PASAJE
      </button>
    </form>

    <!-- RUTA DE PRUEBA -->
    <div v-if="!mostrarResultado" class="test-route-container">
      <p class="test-route-text">¿Quieres probar la animación del Mapa 3D sin un viaje real?</p>
      <button @click="cargarRutaPrueba" class="btn-test-route" type="button">
        <i class="fas fa-route"></i> PROBAR RUTA SIMULADA
      </button>
    </div>
    <div v-if="mostrarResultado && reservaInfo" class="resultado-container">
      <div class="resultado-header">
        <h3><i class="fas fa-check-circle"></i> Pasaje Encontrado</h3>
        <span class="estado-badge confirmado">CONFIRMADO</span>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="label"><i class="fas fa-ticket-alt"></i> Código</span>
          <span class="valor">{{ reservaInfo.id }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-map-marker-alt"></i> Origen</span>
          <span class="valor">{{ reservaInfo.origen }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-map-marker-alt"></i> Destino</span>
          <span class="valor">{{ reservaInfo.destino }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-calendar-alt"></i> Fecha</span>
          <span class="valor">{{ reservaInfo.fechaViaje }}</span>
        </div>
      </div>

      <!-- MAPA INTERACTIVO DE RASTREO -->
      <div class="mapa-rastreo-card">
        <div class="estado-eta">
          <div class="eta-box">
            <span class="eta-title">Tiempo Estimado de Llegada</span>
            <span class="eta-time" :class="{'tiempo-llegado': estadoViaje === 'Llegado', 'tiempo-espera': estadoViaje === 'En Terminal'}">
              {{ estadoViaje === 'En Terminal' ? 'Aún no inicia' : (estadoViaje === 'Llegado' ? '¡Llegó a destino!' : (minutosFaltantes + ' min restantes')) }}
            </span>
            <span v-if="estadoViaje === 'En Tránsito'" class="eta-ciudad">Ubicación aproximada: <strong>{{ ciudadActual }}</strong></span>
          </div>
        </div>

        <div class="mapa-visual-container leaflet-3d-wrapper">
          <div id="mapa-chile" class="leaflet-map-3d"></div>
        </div>

        <div class="horarios-footer">
          <div class="horario-box">
            <span class="horario-label">Salida</span>
            <span class="horario-time">{{ reservaInfo.horarioSalida }}</span>
          </div>
          <div class="horario-box">
            <span class="horario-label">Llegada Estimada</span>
            <span class="horario-time">{{ reservaInfo.horarioLlegada }}</span>
          </div>
        </div>
      </div>

      <div class="acciones">
        <button class="btn-accion primary" @click="mostrarResultado = false; form.codigoReserva = ''">
          <i class="fas fa-search"></i> Rastrear Otro Pasaje
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

export default {
  name: "SeguimientoPasaje",
  data() {
    return {
      form: {
        codigoReserva: "",
        rut: "",
      },
      mostrarResultado: false,
      cargando: false,
      mensajeError: null,
      reservaInfo: null,
      
      minutosFaltantes: 0,
      porcentajeCompletado: 0,
      estadoViaje: 'En Terminal', // 'En Terminal', 'En Tránsito', 'Llegado'
      ciudadActual: '',
      rutaCiudades: [],
      timer: null,

      // Diccionario de rutas para el mapeo
      rutasMap: {
        'Santiago-Concepción': ['Santiago', 'Rancagua', 'Talca', 'Chillán', 'Concepción'],
        'Concepción-Santiago': ['Concepción', 'Chillán', 'Talca', 'Rancagua', 'Santiago'],
        'Chillán-Los Ángeles': ['Chillán', 'Bulnes', 'Cabrero', 'Los Ángeles'],
        'Los Ángeles-Chillán': ['Los Ángeles', 'Cabrero', 'Bulnes', 'Chillán'],
        'Santiago-Valparaíso': ['Santiago', 'Curacaví', 'Casablanca', 'Valparaíso'],
        'Valparaíso-Santiago': ['Valparaíso', 'Casablanca', 'Curacaví', 'Santiago']
      },
      coordenadasCiudades: {
        'Santiago': [-33.4489, -70.6693],
        'Curacaví': [-33.4022, -71.1444],
        'Casablanca': [-33.3168, -71.4057],
        'Valparaíso': [-33.0456, -71.6197],
        'Rancagua': [-34.1708, -70.7444],
        'Talca': [-35.4264, -71.6554],
        'Chillán': [-36.6063, -72.1034],
        'Bulnes': [-36.7423, -72.2985],
        'Cabrero': [-37.0333, -72.4000],
        'Los Ángeles': [-37.4697, -72.3536],
        'Concepción': [-36.8201, -73.0444]
      },
      map: null,
      busMarker: null
    };
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer);
  },
  methods: {
    buscarPasaje() {
      this.cargando = true;
      this.mensajeError = null;
      
      fetch(`${import.meta.env.VITE_API_BASE_URL}/api/reservas/${this.form.codigoReserva}?rutOrEmail=${this.form.rut}`)
        .then(res => {
          if (!res.ok) throw new Error('No pudimos encontrar un pasaje con ese ID de reserva.');
          return res.json();
        })
        .then(data => {
          this.reservaInfo = data;
          this.mostrarResultado = true;
          this.configurarRuta();
          this.$nextTick(() => {
            this.inicializarMapa();
            this.iniciarRastreo();
          });
        })
        .catch(err => {
          alert(err.message);
        })
        .finally(() => {
          this.cargando = false;
        });
    },
    cargarRutaPrueba() {
      // Create a dummy reservation that is 50% completed
      const now = new Date();
      let salida = new Date(now.getTime() - 2 * 60 * 60000); // 2 hours ago
      let llegada = new Date(now.getTime() + 2 * 60 * 60000); // 2 hours from now

      const fSalidaStr = salida.getHours().toString().padStart(2, '0') + ':' + salida.getMinutes().toString().padStart(2, '0');
      const fLlegadaStr = llegada.getHours().toString().padStart(2, '0') + ':' + llegada.getMinutes().toString().padStart(2, '0');

      this.reservaInfo = {
        id: "TEST-MAPA",
        origen: "Santiago",
        destino: "Concepción",
        fechaViaje: now.toISOString().split('T')[0],
        horarioSalida: fSalidaStr,
        horarioLlegada: fLlegadaStr,
        emailContacto: "test@rutaprueba.com"
      };

      this.mostrarResultado = true;
      this.mensajeError = null;
      
      this.configurarRuta();
      this.$nextTick(() => {
        this.inicializarMapa();
        this.iniciarRastreo();
      });
    },
    configurarRuta() {
      const clave = `${this.reservaInfo.origen}-${this.reservaInfo.destino}`;
      if (this.rutasMap[clave]) {
        this.rutaCiudades = this.rutasMap[clave];
      } else {
        this.rutaCiudades = [this.reservaInfo.origen, 'En Ruta', this.reservaInfo.destino];
      }
    },
    iniciarRastreo() {
      if (this.timer) clearInterval(this.timer);
      this.actualizarProgreso();
      this.timer = setInterval(() => {
        this.actualizarProgreso();
      }, 5000);
    },
    inicializarMapa() {
      if (this.map) {
        this.map.remove();
      }
      this.map = L.map('mapa-chile', { zoomControl: false, scrollWheelZoom: false, attributionControl: false }).setView([-35.0, -71.5], 6);
      L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
        maxZoom: 19
      }).addTo(this.map);

      const latlngs = this.rutaCiudades.map(c => this.coordenadasCiudades[c]).filter(c => c);
      if (latlngs.length > 0) {
        L.polyline(latlngs, {color: '#174291', weight: 4, dashArray: '5, 10'}).addTo(this.map);
        
        latlngs.forEach((ll, i) => {
          L.circleMarker(ll, { radius: 6, color: '#ffc107', fillColor: '#fff', fillOpacity: 1 }).addTo(this.map)
            .bindTooltip(this.rutaCiudades[i], { permanent: true, direction: 'right', className: 'map-tooltip' });
        });

        this.map.fitBounds(L.polyline(latlngs).getBounds(), { padding: [50, 50] });

        const busIcon = L.divIcon({ html: '<i class="fas fa-bus map-bus-icon"></i>', className: '', iconSize: [30,30], iconAnchor: [15,15] });
        this.busMarker = L.marker(latlngs[0], { icon: busIcon }).addTo(this.map);
      }
    },
    actualizarMarcadorBus() {
        if (!this.busMarker || !this.rutaCiudades || this.porcentajeCompletado === undefined) return;
        const latlngs = this.rutaCiudades.map(c => this.coordenadasCiudades[c]).filter(c => c);
        if (latlngs.length < 2) return;
        
        let totalSegments = latlngs.length - 1;
        let floatIndex = (this.porcentajeCompletado / 100) * totalSegments;
        let baseIndex = Math.floor(floatIndex);
        let remainder = floatIndex - baseIndex;
        
        if (baseIndex >= totalSegments) {
            this.busMarker.setLatLng(latlngs[latlngs.length - 1]);
        } else {
            let start = latlngs[baseIndex];
            let end = latlngs[baseIndex + 1];
            let currentLat = start[0] + (end[0] - start[0]) * remainder;
            let currentLng = start[1] + (end[1] - start[1]) * remainder;
            this.busMarker.setLatLng([currentLat, currentLng]);
        }
    },
    actualizarProgreso() {
      const now = new Date();
      // Asegurar que nunca sean vacíos o nulos para evitar NaN
      const salidaStr = this.reservaInfo.horarioSalida || '08:00';
      const llegadaStr = this.reservaInfo.horarioLlegada || '16:00';
      
      const horaSalidaDate = this.parsearHora(salidaStr, now);
      let horaLlegadaDate = this.parsearHora(llegadaStr, now);
      
      if(horaLlegadaDate < horaSalidaDate) {
         horaLlegadaDate.setDate(horaLlegadaDate.getDate() + 1);
      }

      const totalMs = horaLlegadaDate - horaSalidaDate;
      const transcurridoMs = now - horaSalidaDate;

      if (transcurridoMs < 0) {
        this.estadoViaje = 'En Terminal';
        this.porcentajeCompletado = 0;
        const faltanMs = Math.abs(transcurridoMs);
        this.minutosFaltantes = Math.round(faltanMs / 60000);
        this.ciudadActual = this.rutaCiudades[0];
      } else if (transcurridoMs >= totalMs) {
        this.estadoViaje = 'Llegado';
        this.porcentajeCompletado = 100;
        this.minutosFaltantes = 0;
        this.ciudadActual = this.rutaCiudades[this.rutaCiudades.length - 1];
      } else {
        this.estadoViaje = 'En Tránsito';
        let porcentaje = (transcurridoMs / totalMs) * 100;
        this.porcentajeCompletado = Math.min(Math.max(porcentaje, 0), 100);
        
        let faltanMs = horaLlegadaDate - now;
        this.minutosFaltantes = Math.max(Math.round(faltanMs / 60000), 1);

        const tramoPorcentaje = 100 / (this.rutaCiudades.length - 1);
        const indiceCiudadActual = Math.floor(this.porcentajeCompletado / tramoPorcentaje);
        this.ciudadActual = this.rutaCiudades[Math.min(indiceCiudadActual, this.rutaCiudades.length - 1)];
      }
      this.actualizarMarcadorBus();
    },
    parsearHora(horaString, baseDate) {
      if(!horaString || typeof horaString !== 'string' || !horaString.includes(':')) {
        horaString = '08:00'; // Default fallback
      }
      const parts = horaString.split(':');
      let horas = parseInt(parts[0]) || 0;
      let minutos = parseInt(parts[1]) || 0;
      let fecha = new Date(baseDate.getTime());
      fecha.setHours(horas, minutos, 0, 0);
      return fecha;
    }
  },
};
</script>

<style scoped>
.form-card {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  padding: 40px;
  width: 90%;
  max-width: 900px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  top: -30px;
}

/* Route Test Styles */
.test-route-container {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px dashed rgba(255, 255, 255, 0.2);
  text-align: center;
}
.test-route-text {
  color: #a4bdf5;
  font-size: 0.95rem;
  margin-bottom: 12px;
}
.btn-test-route {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
  color: #0d286d;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
}
.btn-test-route:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.4);
}


.form-header {
  text-align: center;
  color: white;
  margin-bottom: 35px;
}

.form-header i {
  font-size: 3rem;
  color: #2196f3;
  margin-bottom: 15px;
}

.form-header h2 {
  font-size: 2rem;
  margin-bottom: 10px;
}

.form-header p {
  opacity: 0.9;
  font-size: 1.1rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  margin-bottom: 10px;
  color: white;
  font-size: 0.9rem;
}

label i {
  color: #2196f3;
}

input {
  width: 100%;
  padding: 14px;
  border: 2px solid transparent;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 0.95rem;
  font-family: "Poppins", sans-serif;
  transition: all 0.3s;
  background-color: white;
}

input:focus {
  outline: none;
  border-color: #2196f3;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.2);
}

.btn-submit {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  color: white;
  border: none;
  padding: 16px 40px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  font-size: 1.1rem;
  width: 100%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.4);
}

.btn-submit:active {
  transform: translateY(0);
}

.resultado-container {
  margin-top: 30px;
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.resultado-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.resultado-header h3 {
  color: #0d286d;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.resultado-header h3 i {
  color: #4caf50;
}

.estado-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.85rem;
}

.estado-badge.confirmado {
  background-color: #4caf50;
  color: white;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  color: #666;
  font-size: 0.85rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label i {
  color: #2196f3;
}

.info-item .valor {
  color: #333;
  font-size: 1.1rem;
  font-weight: 500;
}

/* VISTA DE MAPA RASTREO */
.mapa-rastreo-card {
  margin-top: 30px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 30px;
  border: 1px solid #e0e0e0;
}

.estado-eta {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.eta-box {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  color: white;
  padding: 20px 40px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(13, 40, 109, 0.2);
  min-width: 300px;
}

.eta-title {
  display: block;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.eta-time {
  display: block;
  font-size: 2.5rem;
  font-weight: 800;
  color: #ffeb3b;
  margin-bottom: 5px;
}

.eta-time.tiempo-llegado {
  color: #4caf50;
  font-size: 2rem;
}

.eta-time.tiempo-espera {
  color: #ff9800;
  font-size: 2rem;
}

.eta-ciudad {
  display: block;
  font-size: 1.1rem;
  background: rgba(255,255,255,0.1);
  padding: 6px 15px;
  border-radius: 6px;
  margin-top: 10px;
}

.leaflet-3d-wrapper {
  position: relative;
  height: 350px;
  margin: 0 20px 40px 20px;
  perspective: 1200px;
}

.leaflet-map-3d {
  width: 100%;
  height: 100%;
  border-radius: 16px;
  box-shadow: -15px 25px 40px rgba(0,0,0,0.25);
  transform: rotateX(55deg) rotateZ(-25deg);
  transform-style: preserve-3d;
  transition: transform 0.5s ease;
  background: #e5e5e5;
  border: 4px solid white;
  z-index: 1;
}

.leaflet-map-3d:hover {
  transform: rotateX(45deg) rotateZ(-15deg);
}

::v-deep .map-bus-icon {
  color: #ffeb3b;
  font-size: 20px;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
  background: #0d286d;
  padding: 5px;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 4px 10px rgba(0,0,0,0.4);
  display: flex !important;
  align-items: center;
  justify-content: center;
}

::v-deep .map-tooltip {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid #c8d4ef;
  border-radius: 6px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
  font-weight: 700;
  color: #0d286d;
  padding: 4px 8px;
}

.horarios-footer {
  display: flex;
  justify-content: space-around;
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.horario-box {
  display: flex;
  flex-direction: column;
  text-align: center;
}

.horario-label {
  font-size: 0.9rem;
  color: #6c757d;
  text-transform: uppercase;
  margin-bottom: 5px;
}

.horario-time {
  font-size: 1.3rem;
  font-weight: bold;
  color: #212529;
}

@media (max-width: 768px) {
  .form-card {
    padding: 25px 16px;
    top: -20px;
    width: 95%;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .info-grid {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .mapa-visual-container {
    margin: 0 10px 60px 10px;
  }

  .ciudad-name {
    font-size: 0.7rem;
    writing-mode: vertical-rl;
    margin-top: 25px;
  }

  .eta-box {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 4px;
  }

  .eta-time {
    font-size: 1.6rem;
  }

  .horarios-footer {
    padding: 12px;
    gap: 10px;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .btn-test-route {
    width: 100%;
    justify-content: center;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .resultado-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .leaflet-3d-wrapper,
  .leaflet-map-3d {
    transform: none !important; /* Disable 3D tilt on small screens */
    height: 280px;
  }

  input, select {
    font-size: 16px; /* Prevents iOS auto-zoom */
  }
}
</style>

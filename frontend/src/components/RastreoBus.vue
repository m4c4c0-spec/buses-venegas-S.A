<template>
  <div class="rastreo-container">
    <h2>Rastrear mi Bus</h2>
    <p class="instrucciones">Visualiza en tiempo real el recorrido y tiempo estimado de llegada.</p>

    <!-- Formulario de búsqueda -->
    <div v-if="!reservaCargada" class="buscardor-reserva">
      <div class="input-group">
        <label><i class="fas fa-ticket-alt"></i> ID de Reserva:</label>
        <input v-model="idReservaInput" type="text" placeholder="Ej: BB123456" />
      </div>
      <button @click="buscarReserva" class="btn-buscar" :disabled="!idReservaInput || cargando">
        <span v-if="cargando"><i class="fas fa-spinner fa-spin"></i> Buscando...</span>
        <span v-else><i class="fas fa-search"></i> Rastrear</span>
      </button>
      <p v-if="mensajeError" class="error-msg">{{ mensajeError }}</p>
    </div>

    <!-- Vista de Rastreo (Mapa) -->
    <div v-else class="mapa-rastreo-card">
      <div class="rastreo-header">
        <h3>Reserva #{{ reservaInfo.id }}</h3>
        <span class="ruta-badge">{{ reservaInfo.origen }} <i class="fas fa-arrow-right"></i> {{ reservaInfo.destino }}</span>
      </div>

      <div class="estado-eta">
        <div class="eta-box">
          <span class="eta-title">Tiempo Estimado de Llegada</span>
          <span class="eta-time" :class="{'tiempo-llegado': estadoViaje === 'Llegado', 'tiempo-espera': estadoViaje === 'En Terminal'}">
            {{ estadoViaje === 'En Terminal' ? 'Aún no inicia' : (estadoViaje === 'Llegado' ? '¡Llegó a destino!' : (minutosFaltantes + ' min restantes')) }}
          </span>
          <span v-if="estadoViaje === 'En Tránsito'" class="eta-ciudad">Ubicación aproximada: <strong>{{ ciudadActual }}</strong></span>
        </div>
      </div>

      <div class="mapa-visual-container">
        <!-- Barra de progreso base -->
        <div class="progress-track">
          <div class="progress-fill" :style="{ width: porcentajeCompletado + '%' }"></div>
        </div>

        <!-- Ciudades -->
        <div class="ciudades-markers">
          <div v-for="(ciudad, index) in rutaCiudades" :key="index" class="ciudad-marker" :style="{ left: ((index / (rutaCiudades.length - 1)) * 100) + '%' }">
            <div class="marker-dot" :class="{ 'pasado': (index / (rutaCiudades.length - 1)) * 100 <= porcentajeCompletado }"></div>
            <span class="ciudad-name">{{ ciudad }}</span>
          </div>
        </div>

        <!-- Bus animado -->
        <div class="bus-tracker" :style="{ left: porcentajeCompletado + '%' }">
          <i class="fas fa-bus"></i>
        </div>
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

      <button @click="nuevaBusqueda" class="btn-nueva-busqueda">Rastrear otro pasaje</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RastreoBus',
  props: {
    idReservaInicial: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      idReservaInput: this.idReservaInicial,
      cargando: false,
      mensajeError: null,
      reservaCargada: false,
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
      }
    };
  },
  mounted() {
    if (this.idReservaInput) {
      this.buscarReserva();
    }
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer);
  },
  methods: {
    buscarReserva() {
      this.cargando = true;
      this.mensajeError = null;
      // Usamos el punto de acceso de getReserva pasandole el numero y el correo como parametro aunque en el controlador se usa rutOrEmail para validar
      // Pero como aqui para el buscador de rastreo puede que solo el usuario tenga el id, podríamos ajustar el backend o enviar un correo default.
      // Actualmente el backend @GetMapping("/{id}") exige @RequestParam String rutOrEmail. 
      // Por practicidad de simulación, si no tenemos email en input, enviamos vacio o lo bypasseamos. Pero debemos enviar el parámetro.
      fetch(`${import.meta.env.VITE_API_BASE_URL}/api/reservas/${this.idReservaInput}?rutOrEmail=`)
        .then(res => {
          if (!res.ok) throw new Error('No pudimos encontrar un pasaje con ese ID de reserva.');
          return res.json();
        })
        .then(data => {
          this.reservaInfo = data;
          this.reservaCargada = true;
          this.configurarRuta();
          this.iniciarRastreo();
        })
        .catch(err => {
          this.mensajeError = err.message;
        })
        .finally(() => {
          this.cargando = false;
        });
    },
    configurarRuta() {
      const clave = `${this.reservaInfo.origen}-${this.reservaInfo.destino}`;
      if (this.rutasMap[clave]) {
        this.rutaCiudades = this.rutasMap[clave];
      } else {
        // Fallback genérico si la ruta no está en el mapa
        this.rutaCiudades = [this.reservaInfo.origen, 'En Ruta', this.reservaInfo.destino];
      }
    },
    iniciarRastreo() {
      this.actualizarProgreso();
      this.timer = setInterval(() => {
        this.actualizarProgreso();
      }, 10000); // Actualizar cada 10 segundos
    },
    actualizarProgreso() {
      // Como esto es un simulador, basaremos el progreso en la hora REAL del día.
      const now = new Date();
      
      // Para simular fácilmente si la hora del pasaje no coincide con hoy, obligaremos la fecha de los `Date` parseados a HOY.
      const horaSalidaDate = this.parsearHora(this.reservaInfo.horarioSalida, now);
      let horaLlegadaDate = this.parsearHora(this.reservaInfo.horarioLlegada, now);
      
      // Si la llegada es supuestamente "menor" que la salida, asumimos que cruzó la medianoche
      if(horaLlegadaDate < horaSalidaDate) {
         horaLlegadaDate.setDate(horaLlegadaDate.getDate() + 1);
      }

      const totalMs = horaLlegadaDate - horaSalidaDate;
      const transcurridoMs = now - horaSalidaDate;

      if (transcurridoMs < 0) {
        this.estadoViaje = 'En Terminal';
        this.porcentajeCompletado = 0;
        const faltanMs = Math.abs(transcurridoMs); // MS para salir
        this.minutosFaltantes = Math.round(faltanMs / 60000); // minutos para salir
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

        // Estimar ciudad actual basada en el porcentaje completado
        const tramoPorcentaje = 100 / (this.rutaCiudades.length - 1);
        const indiceCiudadActual = Math.floor(this.porcentajeCompletado / tramoPorcentaje);
        this.ciudadActual = this.rutaCiudades[Math.min(indiceCiudadActual, this.rutaCiudades.length - 1)];
      }
    },
    parsearHora(horaString, baseDate) {
      if(!horaString) return new Date();
      const [horas, minutos] = horaString.split(':');
      let fecha = new Date(baseDate.getTime());
      fecha.setHours(parseInt(horas), parseInt(minutos), 0, 0);
      return fecha;
    },
    nuevaBusqueda() {
      if (this.timer) clearInterval(this.timer);
      this.reservaCargada = false;
      this.reservaInfo = null;
      this.idReservaInput = '';
    }
  }
}
</script>

<style scoped>
.rastreo-container {
  background: white;
  padding: 40px;
  width: 90%;
  max-width: 800px;
  margin: -30px auto 40px auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
  position: relative;
  z-index: 10;
}

h2 {
  color: #0d286d;
  margin-bottom: 10px;
}

.instrucciones {
  color: #666;
  margin-bottom: 30px;
}

.buscardor-reserva {
  max-width: 400px;
  margin: 0 auto;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.input-group {
  display: flex;
  flex-direction: column;
  text-align: left;
  margin-bottom: 20px;
}

.input-group label {
  color: #495057;
  font-weight: 600;
  margin-bottom: 8px;
}

.input-group input {
  padding: 12px 15px;
  border: 2px solid #ced4da;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.input-group input:focus {
  border-color: #0d286d;
  outline: none;
}

.btn-buscar {
  background: #0d286d;
  color: white;
  border: none;
  padding: 14px 0;
  width: 100%;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-buscar:hover:not(:disabled) {
  background: #174291;
}

.btn-buscar:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}

.error-msg {
  color: #dc3545;
  margin-top: 15px;
  font-size: 0.9rem;
  font-weight: bold;
}

/* Vista Mapa Rastreo */
.mapa-rastreo-card {
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 30px;
  text-align: left;
}

.rastreo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #f1f3f5;
  padding-bottom: 15px;
  margin-bottom: 25px;
}

.rastreo-header h3 {
  margin: 0;
  color: #212529;
}

.ruta-badge {
  background: #e3f2fd;
  color: #0d286d;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 0.9rem;
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

.mapa-visual-container {
  position: relative;
  height: 80px;
  margin: 0 40px 40px 40px;
}

.progress-track {
  position: absolute;
  top: 30px;
  left: 0;
  right: 0;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
}

.progress-fill {
  height: 100%;
  background: #4caf50; /* Green road */
  border-radius: 4px;
  transition: width 1s linear;
}

.ciudades-markers {
  position: absolute;
  top: 25px;
  left: 0;
  right: 0;
  height: 20px;
}

.ciudad-marker {
  position: absolute;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.marker-dot {
  width: 18px;
  height: 18px;
  background: white;
  border: 4px solid #ced4da;
  border-radius: 50%;
  z-index: 2;
  transition: all 0.5s;
}

.marker-dot.pasado {
  border-color: #4caf50;
  background: #4caf50;
}

.ciudad-name {
  margin-top: 15px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #495057;
  white-space: nowrap;
}

.bus-tracker {
  position: absolute;
  top: 10px; /* Above the line */
  transform: translateX(-50%);
  color: #0d286d;
  font-size: 24px;
  z-index: 3;
  transition: left 1s linear;
  filter: drop-shadow(0 4px 6px rgba(0,0,0,0.2));
}

.horarios-footer {
  display: flex;
  justify-content: space-between;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.horario-box {
  display: flex;
  flex-direction: column;
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

.btn-nueva-busqueda {
  background: transparent;
  color: #0d286d;
  border: 2px solid #0d286d;
  padding: 12px 25px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  display: block;
  margin: 0 auto;
}

.btn-nueva-busqueda:hover {
  background: #e3f2fd;
}

@media (max-width: 768px) {
  .mapa-visual-container {
    margin: 0 10px 60px 10px;
  }
  .ciudad-name {
    font-size: 0.7rem;
    writing-mode: vertical-rl;
    margin-top: 25px;
  }
}
</style>

<template>
  <div class="horarios-container">
    <div class="cabecera">
      <h2>{{ tituloSeccion }}</h2>
      <p class="ruta-info">Viaje desde <span class="destacado">{{ origenActual }}</span> hacia <span class="destacado">{{ destinoActual }}</span></p>
      <p class="fecha-info"><i class="fas fa-calendar-alt"></i> {{ fechaActual }}</p>
      <p class="duracion-info"><i class="fas fa-clock"></i> Tiempo estimado de viaje exacto: <strong>{{ duracionHorasFormateada }}</strong></p>
    </div>

    <div class="horarios-grid">
      <div 
        v-for="(horario, index) in horariosDisponibles" 
        :key="index" 
        class="horario-card"
        :class="{'selected': seleccionadoIndex === index}"
        @click="seleccionadoIndex = index"
      >
        <div class="salida-block">
          <span class="label">Salida</span>
          <div class="hora">
            <i class="fas fa-sun" v-if="horario.salidaHoras >= 6 && horario.salidaHoras < 19" style="color:#ffc107"></i>
            <i class="fas fa-moon" v-else style="color:#0d286d"></i>
            {{ horario.salida }}
          </div>
        </div>

        <div class="trayecto-visual">
          <div class="bus-track">
            <span class="punto-origen"></span>
            <div class="linea-dinamica"></div>
            <div class="bus-wrapper" :class="{'bus-animado': seleccionadoIndex !== index, 'bus-detenido': seleccionadoIndex === index}">
              🚌
            </div>
            <span class="punto-destino"></span>
          </div>
          <span class="duracion-txt">{{ duracionHorasFormateada }} directo</span>
        </div>

        <div class="llegada-block">
          <span class="label">Llegada</span>
          <div class="hora">
            <i class="fas fa-map-marker-alt" style="color:#28a745"></i>
            {{ horario.llegada }}
          </div>
          <span class="dia-siguiente" v-if="horario.diaSiguiente">+1 Día</span>
        </div>
      </div>
    </div>

    <div class="actions">
      <button type="button" @click="$emit('volver')" class="btn-volver"><i class="fas fa-arrow-left"></i> Modificar búsqueda</button>
      <button type="button" @click="continuar" :disabled="seleccionadoIndex === null" class="btn-continuar">
        {{ detallesReserva && detallesReserva.modoCambio ? 'Confirmar Cambio' : 'Elegir Asientos' }} <i class="fas fa-arrow-right"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SeleccionHorario',
  props: {
    detallesReserva: {
      type: Object,
      required: true
    },
    esVuelta: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      seleccionadoIndex: null,
      salidasBase: [9, 12, 15, 18, 21]
    };
  },
  computed: {
    origenActual() {
      return this.esVuelta ? this.detallesReserva.destino : this.detallesReserva.origen;
    },
    destinoActual() {
      return this.esVuelta ? this.detallesReserva.origen : this.detallesReserva.destino;
    },
    fechaActual() {
      return this.esVuelta ? this.detallesReserva.fechaVuelta : this.detallesReserva.fechaIda;
    },
    tituloSeccion() {
      return this.esVuelta ? 'Selecciona tu Horario de Vuelta' : 'Selecciona tu Horario de Viaje';
    },
    duracionHorasDecimal() {
      return this.getExactDuration(this.origenActual, this.destinoActual);
    },
    duracionHorasFormateada() {
      const dec = this.duracionHorasDecimal;
      const hrs = Math.floor(dec);
      const mins = Math.round((dec - hrs) * 60);
      if (mins === 0) return `${hrs} hrs`;
      return `${hrs} hrs ${mins} min`;
    },
    horariosDisponibles() {
      let horarios = this.salidasBase.map(hora => {
        const totalHorasLlegada = hora + this.duracionHorasDecimal;
        const diaSiguiente = totalHorasLlegada >= 24;
        
        let horaLlegadaFormato = Math.floor(totalHorasLlegada % 24);
        let minLlegadaFormato = Math.round((totalHorasLlegada % 1) * 60);

        return {
          salidaHoras: hora,
          salida: `${hora.toString().padStart(2, '0')}:00`,
          horaSalida: `${hora.toString().padStart(2, '0')}:00`,
          llegada: `${horaLlegadaFormato.toString().padStart(2, '0')}:${minLlegadaFormato.toString().padStart(2, '0')}`,
          duracion: this.duracionHorasFormateada,
          diaSiguiente: diaSiguiente
        };
      });
      
      if (this.detallesReserva && this.detallesReserva.modoCambio && this.detallesReserva.horarioOriginal) {
        const horaOriginal = parseInt(this.detallesReserva.horarioOriginal.split(':')[0]);
        horarios = horarios.filter(h => h.salidaHoras > horaOriginal);
      }
      
      return horarios;
    }
  },
  methods: {
    getExactDuration(start, end) {
      if (start === end) return 0;
      const graph = {
        "Antofagasta": { "La Serena": 10.5 },
        "La Serena": { "Antofagasta": 10.5, "Santiago": 5.8 },
        "Valparaíso": { "Santiago": 1.5 },
        "Santiago": { "La Serena": 5.8, "Valparaíso": 1.5, "Rancagua": 1.2 },
        "Rancagua": { "Santiago": 1.2, "Curicó": 1.2 },
        "Curicó": { "Rancagua": 1.2, "Talca": 0.8 },
        "Talca": { "Curicó": 0.8, "Chillán": 1.7 },
        "Chillán": { "Talca": 1.7, "Concepción": 1.3, "Los Ángeles": 1.2 },
        "Concepción": { "Chillán": 1.3 },
        "Los Ángeles": { "Chillán": 1.2, "Victoria": 1.3 },
        "Victoria": { "Los Ángeles": 1.3, "Temuco": 1.0 },
        "Temuco": { "Victoria": 1.0, "Valdivia": 2.5, "Osorno": 3.0 },
        "Valdivia": { "Temuco": 2.5, "Osorno": 1.5 },
        "Osorno": { "Temuco": 3.0, "Valdivia": 1.5, "Puerto Montt": 1.5 },
        "Puerto Montt": { "Osorno": 1.5 }
      };

      let distances = {};
      let visited = new Set();
      for (let node in graph) distances[node] = Infinity;
      distances[start] = 0;

      while (true) {
        let currNode = null;
        let shortest = Infinity;
        for (let node in distances) {
          if (!visited.has(node) && distances[node] < shortest) {
            shortest = distances[node];
            currNode = node;
          }
        }
        if (currNode === null || currNode === end) break;
        visited.add(currNode);

        for (let neighbor in graph[currNode]) {
          let d = distances[currNode] + graph[currNode][neighbor];
          if (d < distances[neighbor]) {
            distances[neighbor] = d;
          }
        }
      }
      return distances[end] === Infinity ? 1 : Math.round(distances[end] * 10) / 10;
    },
    continuar() {
      if (this.seleccionadoIndex !== null) {
        this.$emit('horario-seleccionado', this.horariosDisponibles[this.seleccionadoIndex]);
      }
    }
  }
}
</script>

<style scoped>
.horarios-container {
  background: white;
  padding: 40px;
  width: 90%;
  max-width: 900px;
  margin: -30px auto 40px auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.cabecera {
  text-align: center;
  margin-bottom: 30px;
}

.cabecera h2 {
  color: #0d286d;
  margin-bottom: 10px;
}

.ruta-info {
  font-size: 1.1rem;
  color: #444;
}

.fecha-info {
  margin-top: 5px;
  color: #555;
  font-size: 0.95rem;
}

.destacado {
  color: #174291;
  font-weight: 700;
  text-transform: uppercase;
}

.duracion-info {
  margin-top: 10px;
  color: #666;
  background: #f8f9fa;
  display: inline-block;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 0.95rem;
}

.horarios-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.horario-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 20px 30px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.horario-card:hover {
  border-color: #aec3e8;
  box-shadow: 0 5px 15px rgba(23, 66, 145, 0.08);
  transform: translateY(-2px);
}

.horario-card.selected {
  background: linear-gradient(135deg, #e8f0fe 0%, #f0f4ff 100%);
  border-color: #0d286d;
  box-shadow: 0 5px 20px rgba(13, 40, 109, 0.2);
}

.salida-block, .llegada-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.label {
  font-size: 0.8rem;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 5px;
}

.hora {
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dia-siguiente {
  font-size: 0.75rem;
  background: #dc3545;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 5px;
  font-weight: 600;
}

.trayecto-visual {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20px;
}

.bus-track {
  width: 100%;
  display: flex;
  align-items: center;
  position: relative;
  margin-bottom: 5px;
  height: 30px;
}

.punto-origen, .punto-destino {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ced4da;
  border: 2px solid white;
  box-shadow: 0 0 0 1px #ced4da;
  z-index: 2;
  flex-shrink: 0;
}

.punto-destino {
  background: #0d286d;
  box-shadow: 0 0 0 1px #0d286d;
}

.linea-dinamica {
  flex: 1;
  height: 2px;
  background: repeating-linear-gradient(to right, #ced4da, #ced4da 5px, transparent 5px, transparent 10px);
  position: relative;
}

/* Bus wrapper — positioned absolutely within the track */
.bus-wrapper {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.5rem;
  z-index: 5;
  transition: all 0.4s ease;
  line-height: 1;
}

/* Animated bus — moves left to right */
.bus-wrapper.bus-animado {
  animation: moveBusSmooth 3s infinite ease-in-out;
}

/* Stopped bus — center, bigger, with glow */
.bus-wrapper.bus-detenido {
  animation: busGlow 1.5s infinite ease-in-out;
  left: 50% !important;
  font-size: 2rem;
  filter: drop-shadow(0 3px 6px rgba(13, 40, 109, 0.5));
}

.horario-card.selected .punto-origen {
  background: #28a745;
  box-shadow: 0 0 0 1px #28a745;
}

.horario-card.selected .punto-destino {
  background: #28a745;
  box-shadow: 0 0 0 1px #28a745;
}

.horario-card.selected .linea-dinamica {
  height: 3px;
  background: linear-gradient(to right, #28a745, #0d286d);
}

@keyframes moveBusSmooth {
  0% { left: 15%; }
  50% { left: 85%; }
  100% { left: 15%; }
}

@keyframes busGlow {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.15); }
}

.duracion-txt {
  font-size: 0.8rem;
  color: #666;
  font-weight: 500;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

.btn-volver {
  background: #e9ecef;
  color: #495057;
  border: none;
  padding: 15px 30px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-volver:hover {
  background: #ced4da;
}

.btn-continuar {
  background: #0d286d;
  color: white;
  border: none;
  padding: 15px 40px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-continuar:hover {
  background: #174291;
}

.btn-continuar:disabled {
  background: #ced4da;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .horario-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  .trayecto-visual {
    width: 100%;
  }
}
</style>

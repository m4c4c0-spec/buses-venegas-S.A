<template>
  <div class="asientos-container">
    <h2>Selecciona {{ totalPasajeros }} {{ totalPasajeros === 1 ? 'Asiento' : 'Asientos' }}</h2>
    <p class="instrucciones">Haz clic en los asientos disponibles para seleccionarlos.</p>
    
    <div class="bus-container">
      <div class="delantera-bus">
        <div class="parabrisas"></div>
        <div class="volante-container">
          <i class="fas fa-steering-wheel volante-icon"></i> Cabina
        </div>
      </div>
      <div class="bus-layout">
        <div 
          v-for="asiento in 40" 
          :key="asiento" 
          class="asiento"
          :class="{ 
            'seleccionado': seleccionados.includes(asiento),
            'ocupado': ocupados.includes(asiento),
            'pasillo': asiento % 4 === 2,
            'ventana-izq': asiento % 4 === 1,
            'ventana-der': asiento % 4 === 0
          }"
          @click="toggleAsiento(asiento)"
        >
          <div class="asiento-top"></div>
          <div class="asiento-body">{{ asiento }}</div>
          <div class="apoyabrazos-izq"></div>
          <div class="apoyabrazos-der"></div>
        </div>
      </div>
      <div class="trasera-bus">Baño</div>
    </div>

    <div class="resumen-seleccion">
      <p><strong>Asientos seleccionados:</strong> {{ seleccionados.length > 0 ? seleccionados.join(', ') : 'Ninguno' }}</p>
      <p><strong>Faltan:</strong> {{ totalPasajeros - seleccionados.length }}</p>
    </div>

    <div class="actions">
      <button @click="$emit('volver')" class="btn-volver"><i class="fas fa-arrow-left"></i> Volver</button>
      <button 
        :disabled="seleccionados.length !== totalPasajeros" 
        @click="continuar" 
        class="btn-continuar"
      >
        Continuar a Datos <i class="fas fa-arrow-right"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SeleccionAsientos',
  props: {
    detallesReserva: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      seleccionados: [],
      ocupados: []
    }
  },
  computed: {
    totalPasajeros() {
      return this.detallesReserva && this.detallesReserva.pasajeros ? this.detallesReserva.pasajeros : 1;
    }
  },
  methods: {
    toggleAsiento(n) {
      if (this.ocupados.includes(n)) return; // No hacer nada si está ocupado
      
      if (this.seleccionados.includes(n)) {
        this.seleccionados = this.seleccionados.filter(a => a !== n);
      } else if (this.seleccionados.length < this.totalPasajeros) {
        this.seleccionados.push(n);
        this.seleccionados.sort((a,b) => a-b);
      }
    },
    continuar() {
      if (this.seleccionados.length === this.totalPasajeros) {
        this.$emit('asientos-seleccionados', this.seleccionados);
      }
    }
  },
  mounted() {
    // Generar asientos ocupados aleatorios para simular el requerimiento
    const numOcupados = Math.floor(Math.random() * 10) + 5; // Entre 5 y 15 asientos ocupados
    while(this.ocupados.length < numOcupados) {
      const rnd = Math.floor(Math.random() * 40) + 1;
      if (!this.ocupados.includes(rnd)) {
        this.ocupados.push(rnd);
      }
    }
    
    // Si es cambio de boleto, intentar pre-seleccionar los asientos originales
    if (this.detallesReserva && this.detallesReserva.modoCambio && this.detallesReserva.asientos) {
      let asientosAlerta = [];
      this.detallesReserva.asientos.forEach(asientoOrig => {
        // Asegurarse de quitarlo de ocupados temporalmente para este test, 
        // o si coincide, informar que no está disponible
        if (this.ocupados.includes(asientoOrig)) {
           asientosAlerta.push(asientoOrig);
        } else {
           this.seleccionados.push(asientoOrig);
        }
      });
      
      if (asientosAlerta.length > 0) {
        alert("Algunos de tus asientos originales (" + asientosAlerta.join(', ') + ") ya están ocupados en el nuevo horario. Por favor selecciona unos nuevos.");
      }
    }
  }
}
</script>

<style scoped>
.asientos-container {
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

.bus-container {
  background: #fff;
  border: 4px solid #bbc1c6;
  border-radius: 60px 60px 20px 20px;
  padding: 15px 30px 40px;
  width: max-content;
  margin: 0 auto 30px;
  position: relative;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1), inset 0 0 15px rgba(0,0,0,0.05);
}

.delantera-bus {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 2px dashed #dee2e6;
  padding-bottom: 15px;
}

.parabrisas {
  width: 80%;
  height: 20px;
  background: #e3f2fd;
  border-radius: 20px 20px 0 0;
  margin-bottom: 15px;
  border: 2px solid #90caf9;
  opacity: 0.6;
}

.volante-container {
  align-self: flex-start;
  color: #6c757d;
  font-size: 0.9rem;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8f9fa;
  padding: 8px 15px;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.volante-icon {
  font-size: 1.5rem;
  color: #495057;
}

.bus-layout {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.asiento {
  width: 55px;
  height: 65px;
  background-color: transparent;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.asiento-top {
  width: 80%;
  height: 18px;
  background-color: #6c757d;
  border-radius: 12px 12px 0 0;
  transition: background-color 0.3s;
  box-shadow: inset 0 -2px 5px rgba(0,0,0,0.1);
}

.asiento-body {
  width: 100%;
  height: 40px;
  background-color: #adb5bd;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  color: white;
  font-size: 1.1rem;
  box-shadow: inset 0 -4px 5px rgba(0,0,0,0.15), 0 4px 6px rgba(0,0,0,0.1);
  transition: all 0.3s;
  position: relative;
  z-index: 2;
}

.apoyabrazos-izq, .apoyabrazos-der {
  position: absolute;
  width: 6px;
  height: 25px;
  background: #495057;
  top: 25px;
  border-radius: 3px;
  transition: background-color 0.3s;
}

.apoyabrazos-izq { left: -3px; }
.apoyabrazos-der { right: -3px; }

.asiento:hover .asiento-body {
  transform: translateY(-2px);
  background-color: #868e96;
}
.asiento:hover .asiento-top { background-color: #495057; }

.asiento.seleccionado .asiento-body {
  background-color: #ffc107;
  color: #0d286d;
  box-shadow: inset 0 -4px 5px rgba(255,152,0,0.3), 0 0 15px rgba(255,193,7,0.6);
}

.asiento.seleccionado .asiento-top {
  background-color: #ff9800;
}

.asiento.seleccionado .apoyabrazos-izq, .asiento.seleccionado .apoyabrazos-der {
  background-color: #f57c00;
}

.asiento.ocupado {
  cursor: not-allowed;
  opacity: 0.6;
}

.asiento.ocupado .asiento-body {
  background-color: #cfd8dc; /* Gray out */
  color: #90a4ae;
  box-shadow: none;
}

.asiento.ocupado .asiento-top, .asiento.ocupado .apoyabrazos-izq, .asiento.ocupado .apoyabrazos-der {
  background-color: #b0bec5;
}

.asiento.pasillo {
  margin-right: 50px;
}

.trasera-bus {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px dashed #dee2e6;
  text-align: right;
  color: #6c757d;
  font-weight: bold;
  font-size: 0.9rem;
}

.resumen-seleccion {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 30px;
  text-align: left;
  display: flex;
  justify-content: space-around;
  font-size: 1.1rem;
}

.actions {
  display: flex;
  justify-content: space-between;
  gap: 20px;
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

.btn-continuar:hover:not(:disabled) {
  background: #174291;
}

.btn-continuar:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .bus-container {
    padding: 20px 10px;
    border-width: 2px;
  }
  .asiento {
    width: 40px;
    height: 40px;
  }
  .asiento.pasillo {
    margin-right: 20px;
  }
}
</style>

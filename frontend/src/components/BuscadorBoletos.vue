<template>
  <div class="form-card">
    <form @submit.prevent="buscarPasajes">
      <div class="form-row">
        <div class="form-group">
          <label for="origen">
            <i class="fas fa-map-marker-alt"></i> Origen
          </label>
          <input
              type="text"
              id="origen"
              placeholder="¿Desde dónde viajas?"
              v-model="form.origen"
              required
              :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="destino">
            <i class="fas fa-map-marker-alt"></i> Destino
          </label>
          <input
              type="text"
              id="destino"
              placeholder="¿A dónde vas?"
              v-model="form.destino"
              required
              :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="fechaIda">
            <i class="fas fa-calendar-alt"></i> Fecha Ida
          </label>
          <input
              type="date"
              id="fechaIda"
              v-model="form.fechaIda"
              :min="fechaMinima"
              required
              :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="fechaRegreso">
            <i class="fas fa-calendar-alt"></i> Fecha Regreso <span class="opcional">(Opcional)</span>
          </label>
          <input
              type="date"
              id="fechaRegreso"
              v-model="form.fechaRegreso"
              :min="form.fechaIda || fechaMinima"
              :disabled="loading"
          />
        </div>
      </div>

      <button type="submit" class="btn-buscar" :disabled="loading">
        <i class="fas fa-search"></i> 
        {{ loading ? 'BUSCANDO...' : 'BUSCAR PASAJES' }}
      </button>

      <div class="opciones-adicionales">
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.soloIda" :disabled="loading">
          <span>Solo Ida</span>
        </label>
      </div>
    </form>

    <!-- Mensaje de error -->
    <div v-if="error" class="error-message">
      <i class="fas fa-exclamation-circle"></i>
      {{ error }}
    </div>

    <!-- Resultados de búsqueda -->
    <div v-if="trips.length > 0" class="resultados-container">
      <h3>Viajes Disponibles ({{ trips.length }})</h3>
      <div class="viajes-lista">
        <div v-for="trip in trips" :key="trip.id" class="viaje-card">
          <div class="viaje-header">
            <div class="ruta">
              <span class="ciudad">{{ trip.origin }}</span>
              <i class="fas fa-arrow-right"></i>
              <span class="ciudad">{{ trip.destination }}</span>
            </div>
            <div class="precio">{{ formatCurrency(trip.basePrice) }}</div>
          </div>
          <div class="viaje-details">
            <div class="detail-item">
              <i class="fas fa-calendar"></i>
              {{ formatDate(trip.departureTime) }}
            </div>
            <div class="detail-item">
              <i class="fas fa-clock"></i>
              Salida: {{ formatTime(trip.departureTime) }}
            </div>
            <div class="detail-item">
              <i class="fas fa-clock"></i>
              Llegada: {{ formatTime(trip.arrivalTime) }}
            </div>
            <div class="detail-item">
              <i class="fas fa-chair"></i>
              {{ trip.availableSeats }} asientos disponibles
            </div>
          </div>
          <button class="btn-seleccionar" @click="seleccionarViaje(trip)">
            <i class="fas fa-check"></i> Seleccionar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useTrips } from '../composables/useTrips';
import { formatCurrency, formatDate, formatTime } from '../utils/formatters';
import type { TripResponse } from '../types/trip';
import "../assets/BusacadorBoletos.css";

// Composable para gestionar viajes
const { trips, loading, error, searchTrips } = useTrips();

// Estado del formulario
const form = ref({
  origen: '',
  destino: '',
  fechaIda: '',
  fechaRegreso: '',
  soloIda: false
});

// Fecha mínima (hoy)
const fechaMinima = new Date().toISOString().split('T')[0];

/**
 * Buscar pasajes
 */
const buscarPasajes = async () => {
  if (!form.value.origen || !form.value.destino || !form.value.fechaIda) {
    alert('Por favor completa todos los campos requeridos');
    return;
  }

  await searchTrips(form.value.origen, form.value.destino, form.value.fechaIda);
};

/**
 * Seleccionar un viaje
 */
const seleccionarViaje = (trip: TripResponse) => {
  console.log('Viaje seleccionado:', trip);
  alert(`Viaje seleccionado: ${trip.origin} → ${trip.destination}\nPrecio: ${formatCurrency(trip.basePrice)}`);
  // Aquí se podría navegar a la página de selección de asientos
  // o emitir un evento para el componente padre
};
</script>

<style scoped>
.error-message {
  background-color: rgba(244, 67, 54, 0.1);
  border-left: 4px solid #f44336;
  padding: 15px;
  margin-top: 20px;
  border-radius: 8px;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-message i {
  color: #f44336;
  font-size: 1.2rem;
}

.resultados-container {
  margin-top: 30px;
  animation: fadeIn 0.5s ease-in;
}

.resultados-container h3 {
  color: white;
  margin-bottom: 20px;
  font-size: 1.5rem;
}

.viajes-lista {
  display: grid;
  gap: 15px;
}

.viaje-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.viaje-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.viaje-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.ruta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2rem;
  font-weight: 600;
  color: #0d286d;
}

.ruta i {
  color: #ffeb3b;
}

.ciudad {
  color: #0d286d;
}

.precio {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4caf50;
}

.viaje-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 0.9rem;
}

.detail-item i {
  color: #0d286d;
}

.btn-seleccionar {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  width: 100%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-seleccionar:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
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
</style>
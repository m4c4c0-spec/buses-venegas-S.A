<template>
  <div class="buses-list">
    <div v-if="loading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      <p>Buscando viajes disponibles...</p>
    </div>

    <div v-else-if="trips.length === 0" class="empty-state">
      <i class="fas fa-road"></i>
      <p>No se encontraron viajes para esta ruta y fecha.</p>
    </div>

    <div 
      v-else
      v-for="trip in trips" 
      :key="trip.id" 
      class="bus-card"
      :class="{ selected: selectedTripId === trip.id }"
      @click="$emit('select-trip', trip)"
    >
      <div class="bus-info">
        <div class="bus-route">
          <span>{{ trip.origin }}</span>
          <i class="fas fa-arrow-right"></i>
          <span>{{ trip.destination }}</span>
        </div>
        <div class="bus-times">
          <div class="time-item">
            <i class="fas fa-clock"></i>
            <span>{{ new Date(trip.departureTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) }}</span>
          </div>
          <div class="time-item">
            <i class="fas fa-calendar"></i>
            <span>{{ new Date(trip.departureTime).toLocaleDateString() }}</span>
          </div>
        </div>
        <div class="bus-seats">
          <i class="fas fa-couch"></i>
          <span>{{ trip.availableSeats }} asientos disp.</span>
        </div>
      </div>

      <div class="bus-price">
        <div class="price">${{ trip.basePrice.toLocaleString() }}</div>
        <span class="per-person">por persona</span>
      </div>

      <div class="select-indicator">
        <i v-if="selectedTripId === trip.id" class="fas fa-check-circle"></i>
        <i v-else class="far fa-circle"></i>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { TripResponse } from '../types/trip';

defineProps<{
  trips: TripResponse[];
  loading: boolean;
  selectedTripId?: number;
}>();

defineEmits<{
  (e: 'select-trip', trip: TripResponse): void;
}>();
</script>

<style scoped>
/* Styles inherited from BuscadorBoletos or generic styles */
.buses-list {
  display: grid;
  gap: 15px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

.bus-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 20px;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 3px solid transparent;
}

.bus-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.bus-card.selected {
  border-color: #4caf50;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
}

.bus-route {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2rem;
  font-weight: 600;
  color: #0d286d;
  margin-bottom: 10px;
}

.bus-times {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 0.9rem;
}

.bus-seats {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4caf50;
  font-size: 0.9rem;
}

.bus-price .price {
  font-size: 1.6rem;
  font-weight: 700;
  color: #4caf50;
}

.select-indicator {
  font-size: 1.5rem;
  color: #ddd;
}

.bus-card.selected .select-indicator {
  color: #4caf50;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 40px;
  color: white;
}
.loading-state i, .empty-state i {
  font-size: 3rem;
  margin-bottom: 20px;
  color: #ffeb3b;
}
</style>

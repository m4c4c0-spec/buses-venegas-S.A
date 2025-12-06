<template>
  <div class="seat-container">
    <div class="bus-layout">
      <div class="bus-front">
        <i class="fas fa-steering-wheel"></i>
        <span>Frente del Bus</span>
      </div>

      <div class="seats-grid">
        <div 
          v-for="seat in seats" 
          :key="seat.number || 'aisle-' + Math.random()"
          class="seat-wrapper"
        >
          <div 
            v-if="!seat.isAisle"
            class="seat"
            :class="{ 
              occupied: seat.occupied, 
              selected: selectedSeats.includes(seat.number) 
            }"
            @click="toggleSeat(seat)"
          >
            {{ seat.number }}
          </div>
          <div v-else class="seat aisle"></div>
        </div>
      </div>
    </div>

    <div class="seat-legend">
      <div class="legend-item">
        <div class="seat-sample available"></div>
        <span>Disponible</span>
      </div>
      <div class="legend-item">
        <div class="seat-sample selected"></div>
        <span>Seleccionado</span>
      </div>
      <div class="legend-item">
        <div class="seat-sample occupied"></div>
        <span>Ocupado</span>
      </div>
    </div>
    
    <div v-if="selectedSeats.length > 0" class="selection-summary">
        <p>Asientos seleccionados: <strong>{{ selectedSeats.join(', ') }}</strong></p>
        <p>Total: <strong>${{ (selectedSeats.length * pricePerSeat).toLocaleString() }}</strong></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue';

// defineProps and defineEmits are valid in script setup without import

interface Seat {
  number: string;
  isAisle: boolean;
  occupied: boolean;
}

const props = defineProps<{
  seats: Seat[];
  selectedSeats: string[];
  maxSeats: number;
  pricePerSeat: number;
}>();

const emit = defineEmits<{
  (e: 'update:selectedSeats', seats: string[]): void;
}>();

const toggleSeat = (seat: Seat) => {
  if (seat.occupied) return;

  const currentSelection = [...props.selectedSeats];
  const index = currentSelection.indexOf(seat.number);

  if (index !== -1) {
    currentSelection.splice(index, 1);
  } else {
    // Basic logic: if max reached, remove first and add new (better UX than blocking)
    // Or just block if strictly enforcing "Select N seats".
    // Let's toggle behavior: if max > 1 and already full, warn or replace?
    // Given the step "Select N passengers", we should enforce exactly N or up to N?
    // BuscadorBoletos says "Select {{ passengers }} seats".
    if (currentSelection.length >= props.maxSeats) {
        // Option: Remove the first one to allow "move" behavior
        currentSelection.shift();
    }
    currentSelection.push(seat.number);
  }

  emit('update:selectedSeats', currentSelection);
};
</script>

<style scoped>
.bus-layout {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  max-width: 400px;
  margin: 0 auto;
}

.bus-front {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px dashed rgba(255, 255, 255, 0.3);
  justify-content: center;
}

.seats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  justify-items: center;
}

.seat {
  width: 40px;
  height: 40px;
  border-radius: 8px 8px 4px 4px;
  background: #4caf50;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

.seat:hover:not(.occupied) {
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.seat.occupied {
  background: #9e9e9e;
  cursor: not-allowed;
  opacity: 0.8;
  box-shadow: none;
}

.seat.selected {
  background: #ffeb3b;
  color: #0d286d;
  font-weight: bold;
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(255, 235, 59, 0.5);
  border: 2px solid #fff;
}

.seat.aisle {
  background: transparent;
  cursor: default;
  box-shadow: none;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 25px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 0.9rem;
}

.seat-sample {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.seat-sample.available { background: #4caf50; }
.seat-sample.selected { background: #ffeb3b; }
.seat-sample.occupied { background: #9e9e9e; }

.selection-summary {
  margin-top: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.selection-summary p {
  margin: 5px 0;
}
</style>

<template>
  <div class="purchase-wizard">
    <!-- Progress Steps -->
    <div class="wizard-progress">
      <div 
        v-for="(step, index) in steps" 
        :key="index"
        class="progress-step"
        :class="{ active: currentStep === index, completed: currentStep > index }"
      >
        <div class="step-number">
          <i v-if="currentStep > index" class="fas fa-check"></i>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <span class="step-label">{{ step }}</span>
      </div>
    </div>

    <div class="wizard-content">
      <!-- PASO 1: Buscar Buses -->
      <div v-if="currentStep === 0" class="step-container">
        <div class="step-header">
          <i class="fas fa-search"></i>
          <h2>Buscar tu Viaje</h2>
          <p>Ingresa tu origen, destino y fecha de viaje</p>
        </div>

        <form @submit.prevent="buscarViajes">
          <div class="form-row">
            <div class="form-group">
              <label><i class="fas fa-map-marker-alt"></i> Origen</label>
              <input type="text" v-model="searchForm.origen" placeholder="¿Desde dónde viajas?" required />
            </div>
            <div class="form-group">
              <label><i class="fas fa-map-marker-alt"></i> Destino</label>
              <input type="text" v-model="searchForm.destino" placeholder="¿A dónde vas?" required />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label><i class="fas fa-calendar-alt"></i> Fecha de Ida</label>
              <input type="date" v-model="searchForm.fechaIda" :min="fechaMinima" required />
            </div>
            <div class="form-group">
              <label><i class="fas fa-users"></i> Pasajeros</label>
              <select v-model="searchForm.pasajeros">
                <option v-for="n in 6" :key="n" :value="n">{{ n }} {{ n === 1 ? 'pasajero' : 'pasajeros' }}</option>
              </select>
            </div>
          </div>
          
          <!-- Error message -->
          <div v-if="error" class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            {{ error }}
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            <i class="fas fa-search"></i>
            {{ loading ? 'BUSCANDO...' : 'BUSCAR BUSES' }}
          </button>
        </form>
      </div>

      <!-- PASO 2: Seleccionar Bus -->
      <div v-if="currentStep === 1" class="step-container">
        <div class="step-header">
          <i class="fas fa-bus"></i>
          <h2>Selecciona tu Bus</h2>
          <p>{{ trips.length }} buses disponibles para tu ruta</p>
        </div>

        <div class="buses-list">
          <div 
            v-for="trip in trips" 
            :key="trip.id" 
            class="bus-card"
            :class="{ selected: selectedTrip?.id === trip.id }"
            @click="selectTrip(trip)"
          >
            <div class="bus-info">
              <div class="bus-route">
                <span class="city">{{ trip.origin }}</span>
                <i class="fas fa-arrow-right"></i>
                <span class="city">{{ trip.destination }}</span>
              </div>
              <div class="bus-times">
                <div class="time-item">
                  <i class="fas fa-clock"></i>
                  <span>Salida: {{ formatTime(trip.departureTime) }}</span>
                </div>
                <div class="time-item">
                  <i class="fas fa-clock"></i>
                  <span>Llegada: {{ formatTime(trip.arrivalTime) }}</span>
                </div>
              </div>
              <div class="bus-seats">
                <i class="fas fa-chair"></i>
                <span>{{ trip.availableSeats }} asientos disponibles</span>
              </div>
            </div>
            <div class="bus-price">
              <span class="price">{{ formatCurrency(trip.basePrice) }}</span>
              <span class="per-person">/persona</span>
            </div>
            <div class="select-indicator">
              <i :class="selectedTrip?.id === trip.id ? 'fas fa-check-circle' : 'far fa-circle'"></i>
            </div>
          </div>
        </div>

        <div class="step-actions">
          <button class="btn-secondary" @click="prevStep">
            <i class="fas fa-arrow-left"></i> Volver
          </button>
          <button class="btn-primary" @click="nextStep" :disabled="!selectedTrip">
            Continuar <i class="fas fa-arrow-right"></i>
          </button>
        </div>
      </div>

      <!-- PASO 3: Seleccionar Asientos -->
      <div v-if="currentStep === 2" class="step-container">
        <div class="step-header">
          <i class="fas fa-chair"></i>
          <h2>Selecciona tus Asientos</h2>
          <p>Selecciona {{ searchForm.pasajeros }} asiento(s)</p>
        </div>

        <div class="seat-selection">
          <div class="bus-layout">
            <div class="bus-front">
              <i class="fas fa-steering-wheel"></i>
              <span>Conductor</span>
            </div>
            <div class="seats-grid">
              <div 
                v-for="seat in currentSeats" 
                :key="seat.number"
                class="seat"
                :class="{ 
                  occupied: seat.occupied, 
                  selected: selectedSeats.includes(seat.number),
                  aisle: seat.isAisle
                }"
                @click="!seat.occupied && !seat.isAisle && toggleSeat(seat.number)"
              >
                <span v-if="!seat.isAisle">{{ seat.number }}</span>
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

          <div class="selection-summary">
            <p><strong>Asientos seleccionados:</strong> {{ selectedSeats.length > 0 ? selectedSeats.join(', ') : 'Ninguno' }}</p>
            <p><strong>Total:</strong> {{ formatCurrency(calculateTotal()) }}</p>
          </div>
        </div>

        <!-- Error de reserva -->
        <div v-if="bookingError" class="error-message">
          <i class="fas fa-exclamation-circle"></i>
          {{ bookingError }}
        </div>

        <div class="step-actions">
          <button class="btn-secondary" @click="prevStep" :disabled="creatingBooking">
            <i class="fas fa-arrow-left"></i> Volver
          </button>
          <button class="btn-primary" @click="goToPayment" :disabled="selectedSeats.length !== searchForm.pasajeros || creatingBooking">
            <i :class="creatingBooking ? 'fas fa-spinner fa-spin' : 'fas fa-credit-card'"></i>
            {{ creatingBooking ? 'Creando reserva...' : 'Ir a Pagar' }}
          </button>
        </div>
      </div>

      <!-- PASO 4: Confirmación -->
      <div v-if="currentStep === 3" class="step-container confirmation">
        <div class="success-header">
          <div class="success-icon">
            <i class="fas fa-check-circle"></i>
          </div>
          <h2>¡Compra Exitosa!</h2>
          <p>Tu reserva ha sido confirmada</p>
        </div>

        <div class="confirmation-details">
          <div class="detail-row">
            <span class="label">Código de Reserva:</span>
            <span class="value code">{{ bookingCode }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Ruta:</span>
            <span class="value">{{ selectedTrip?.origin }} → {{ selectedTrip?.destination }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Fecha:</span>
            <span class="value">{{ formatDate(selectedTrip?.departureTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Asientos:</span>
            <span class="value">{{ selectedSeats.join(', ') }}</span>
          </div>
          <div class="detail-row">
            <span class="label">Transacción:</span>
            <span class="value code">{{ paymentTransactionId }}</span>
          </div>
          <div class="detail-row total">
            <span class="label">Total Pagado:</span>
            <span class="value price">{{ formatCurrency(calculateTotal()) }}</span>
          </div>
        </div>

        <div class="info-notice">
          <i class="fas fa-envelope"></i>
          <span>Recibirás un correo con tu código QR para abordar</span>
        </div>

        <button class="btn-primary" @click="resetWizard">
          <i class="fas fa-plus"></i> Nueva Compra
        </button>
      </div>
    </div>

    <!-- Modal de Pago -->
    <PaymentModal
      :is-open="showPaymentModal"
      :booking="bookingForPayment"
      @close="showPaymentModal = false"
      @payment-complete="onPaymentComplete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useTrips } from '../composables/useTrips';
import { formatCurrency, formatDate, formatTime } from '../utils/formatters';
import { bookingService } from '../services/bookingService';
import type { TripResponse } from '../types/trip';
import type { BookingResponse } from '../types/booking';
import PaymentModal from './PaymentModal.vue';
import "../assets/BusacadorBoletos.css";

// Steps
const steps = ['Buscar', 'Seleccionar Bus', 'Elegir Asientos', 'Confirmación'];
const currentStep = ref(0);

// Composable
const { trips, loading, error, searchTrips } = useTrips();

// Loading state for booking
const creatingBooking = ref(false);
const bookingError = ref<string | null>(null);

// Form state
const searchForm = ref({
  origen: '',
  destino: '',
  fechaIda: '',
  pasajeros: 1,
});

const fechaMinima = new Date().toISOString().split('T')[0];

// Selection state
const selectedTrip = ref<TripResponse | null>(null);
const selectedSeats = ref<string[]>([]);
const currentSeats = ref<any[]>([]);

// Payment state
const showPaymentModal = ref(false);
const bookingCode = ref('');
const paymentTransactionId = ref('');

const bookingForPayment = ref<BookingResponse>({
  id: 0,
  tripId: 0,
  userId: 0,
  seats: [],
  status: 'PENDING',
  totalAmount: 0,
  createdAt: '',
  expiresAt: '',
});

// Methods
const buscarViajes = async () => {
  if (!searchForm.value.origen || !searchForm.value.destino || !searchForm.value.fechaIda) {
    return;
  }
  
  await searchTrips(searchForm.value.origen, searchForm.value.destino, searchForm.value.fechaIda);
  
  // Solo avanzar si hay resultados del API
  if (trips.value.length > 0) {
    currentStep.value = 1;
  }
};

const selectTrip = (trip: TripResponse) => {
  selectedTrip.value = trip;
  // Generar asientos al seleccionar el viaje
  currentSeats.value = generateSeats(40);
};

const generateSeats = (total: number) => {
  const seats = [];
  
  // Generar asientos ocupados aleatoriamente (20-40% de ocupación)
  const occupationRate = 0.2 + Math.random() * 0.2;
  const numOccupied = Math.floor(total * occupationRate);
  const occupiedSeats = new Set<number>();
  
  while (occupiedSeats.size < numOccupied) {
    occupiedSeats.add(Math.floor(Math.random() * total) + 1);
  }
  
  for (let i = 1; i <= total + 10; i++) { // +10 for aisles
    const row = Math.floor((i - 1) / 5);
    const col = (i - 1) % 5;
    
    // Column 2 is aisle
    if (col === 2) {
      seats.push({ number: '', isAisle: true, occupied: false });
    } else {
      const seatNum = seats.filter(s => !s.isAisle).length + 1;
      if (seatNum <= total) {
        seats.push({
          number: seatNum.toString(),
          isAisle: false,
          occupied: occupiedSeats.has(seatNum),
        });
      }
    }
  }
  
  return seats.slice(0, 50); // Limit to grid
};

const toggleSeat = (seatNumber: string) => {
  const index = selectedSeats.value.indexOf(seatNumber);
  if (index > -1) {
    selectedSeats.value.splice(index, 1);
  } else if (selectedSeats.value.length < searchForm.value.pasajeros) {
    selectedSeats.value.push(seatNumber);
  }
};

const calculateTotal = () => {
  if (!selectedTrip.value) return 0;
  return selectedTrip.value.basePrice * selectedSeats.value.length;
};

const goToPayment = async () => {
  if (!selectedTrip.value || selectedSeats.value.length === 0) return;
  
  creatingBooking.value = true;
  bookingError.value = null;
  
  try {
    // Crear booking real en el backend
    const bookingRequest = {
      tripId: selectedTrip.value.id.toString(),
      userId: '1', // En producción vendría del usuario autenticado
      seatNumber: selectedSeats.value[0], // Backend espera un asiento, los demás se agregan
      passenger: {
        name: 'Pasajero',
        rut: '12345678-9',
        email: 'pasajero@email.com',
      },
    };
    
    // Llamar al API para crear la reserva
    const createdBooking = await bookingService.createBooking(bookingRequest);
    
    console.log('Reserva creada en BD:', createdBooking);
    
    // Usar el booking real del backend
    bookingForPayment.value = {
      id: createdBooking.id,
      tripId: createdBooking.tripId,
      userId: createdBooking.userId,
      seats: selectedSeats.value, // Usar los asientos seleccionados en el frontend
      status: createdBooking.status,
      totalAmount: calculateTotal(),
      createdAt: createdBooking.createdAt,
      expiresAt: createdBooking.expiresAt,
    };
    
    bookingCode.value = `BV-${createdBooking.id.toString().padStart(8, '0')}`;
    showPaymentModal.value = true;
    
  } catch (err: any) {
    console.error('Error creando reserva:', err);
    bookingError.value = err.response?.data?.message || 'Error al crear la reserva';
  } finally {
    creatingBooking.value = false;
  }
};

const onPaymentComplete = (transactionId: string) => {
  paymentTransactionId.value = transactionId;
  bookingCode.value = `BV-${Date.now().toString().slice(-8)}`;
  showPaymentModal.value = false;
  currentStep.value = 3;
};

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
};

const nextStep = () => {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++;
  }
};

const resetWizard = () => {
  currentStep.value = 0;
  selectedTrip.value = null;
  selectedSeats.value = [];
  trips.value = [];
  searchForm.value = {
    origen: '',
    destino: '',
    fechaIda: '',
    pasajeros: 1,
  };
};
</script>

<style scoped>
.purchase-wizard {
  padding: 20px 0;
}

/* Progress Bar */
.wizard-progress {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 30px;
  padding: 0 20px;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
  max-width: 150px;
  position: relative;
}

.progress-step::after {
  content: '';
  position: absolute;
  top: 18px;
  left: calc(50% + 20px);
  width: calc(100% - 40px);
  height: 3px;
  background: rgba(255, 255, 255, 0.2);
}

.progress-step:last-child::after {
  display: none;
}

.progress-step.completed::after {
  background: #4caf50;
}

.step-number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  z-index: 1;
  transition: all 0.3s;
}

.progress-step.active .step-number {
  background: #ffeb3b;
  color: #0d286d;
  transform: scale(1.1);
}

.progress-step.completed .step-number {
  background: #4caf50;
}

.step-label {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
  text-align: center;
}

.progress-step.active .step-label {
  color: white;
  font-weight: 600;
}

/* Wizard Content */
.wizard-content {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  border-radius: 16px;
  padding: 30px;
  max-width: 900px;
  margin: 0 auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.step-container {
  animation: fadeIn 0.3s ease;
}

.step-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.step-header i {
  font-size: 2.5rem;
  color: #ffeb3b;
  margin-bottom: 15px;
}

.step-header h2 {
  margin: 0 0 10px 0;
}

.step-header p {
  opacity: 0.8;
  margin: 0;
}

/* Form Styles */
.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: white;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-group label i {
  color: #ffeb3b;
}

.form-group input,
.form-group select {
  padding: 14px;
  border: 2px solid transparent;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #ffeb3b;
}

/* Buttons */
.btn-primary {
  background: linear-gradient(135deg, #ffeb3b 0%, #ffc107 100%);
  color: #0d286d;
  border: none;
  padding: 16px 32px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  transition: all 0.3s;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 235, 59, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 14px 28px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-secondary:hover {
  border-color: white;
  background: rgba(255, 255, 255, 0.1);
}

.step-actions {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-top: 30px;
}

.step-actions .btn-primary {
  flex: 1;
  max-width: 250px;
}

/* Bus List */
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

.bus-route i {
  color: #ffeb3b;
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

.time-item i {
  color: #0d286d;
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

.bus-price .per-person {
  font-size: 0.85rem;
  color: #666;
}

.select-indicator {
  font-size: 1.5rem;
  color: #ddd;
}

.bus-card.selected .select-indicator {
  color: #4caf50;
}

/* Seat Selection */
.seat-selection {
  display: grid;
  gap: 30px;
}

.bus-layout {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
}

.bus-front {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px dashed rgba(255, 255, 255, 0.3);
}

.bus-front i {
  font-size: 1.5rem;
}

.seats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
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
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.seat:hover:not(.occupied):not(.aisle) {
  transform: scale(1.1);
}

.seat.occupied {
  background: #9e9e9e;
  cursor: not-allowed;
}

.seat.selected {
  background: #ffeb3b;
  color: #0d286d;
  transform: scale(1.1);
}

.seat.aisle {
  background: transparent;
  cursor: default;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
}

.seat-sample {
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.seat-sample.available {
  background: #4caf50;
}

.seat-sample.selected {
  background: #ffeb3b;
}

.seat-sample.occupied {
  background: #9e9e9e;
}

.selection-summary {
  background: rgba(255, 235, 59, 0.2);
  border-radius: 10px;
  padding: 15px 20px;
  color: white;
}

.selection-summary p {
  margin: 5px 0;
}

/* Confirmation */
.confirmation {
  text-align: center;
}

.success-header {
  margin-bottom: 30px;
}

.success-icon {
  font-size: 5rem;
  color: #4caf50;
  margin-bottom: 20px;
  animation: scaleIn 0.5s ease;
}

.confirmation h2 {
  color: white;
  margin: 0;
}

.confirmation p {
  color: rgba(255, 255, 255, 0.8);
}

.confirmation-details {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  text-align: left;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  color: rgba(255, 255, 255, 0.7);
}

.detail-row .value {
  font-weight: 600;
}

.detail-row .value.code {
  font-family: monospace;
  background: rgba(255, 235, 59, 0.2);
  padding: 4px 10px;
  border-radius: 4px;
  color: #ffeb3b;
}

.detail-row .value.price {
  color: #ffeb3b;
  font-size: 1.3rem;
}

.detail-row.total {
  margin-top: 10px;
  padding-top: 15px;
  border-top: 2px solid rgba(255, 235, 59, 0.3);
}

.info-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: rgba(76, 175, 80, 0.2);
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 25px;
  color: white;
}

.info-notice i {
  color: #4caf50;
  font-size: 1.3rem;
}

.error-message {
  background: rgba(244, 67, 54, 0.2);
  border-left: 4px solid #f44336;
  padding: 15px;
  margin: 20px 0;
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

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes scaleIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* Responsive */
@media (max-width: 768px) {
  .wizard-progress {
    flex-wrap: wrap;
  }
  
  .progress-step::after {
    display: none;
  }
  
  .bus-card {
    grid-template-columns: 1fr;
  }
  
  .seats-grid {
    grid-template-columns: repeat(5, 35px);
  }
  
  .seat {
    width: 35px;
    height: 35px;
  }
}
</style>
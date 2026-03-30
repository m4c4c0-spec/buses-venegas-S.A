<template>
  <div class="form-card">
    <div class="form-header">
      <i class="fas fa-check-circle"></i>
      <h2>Confirma tu Pasaje</h2>
      <p>Verifica y confirma tu reserva</p>
    </div>

    <form @submit.prevent="confirmarPasaje">
      <div class="form-row">
        <div class="form-group full-width">
          <label for="codigoReserva">
            <i class="fas fa-ticket-alt"></i> Código de Reserva
          </label>
          <input
              type="text"
              id="codigoReserva"
              placeholder="Ejemplo: BB123456789"
              v-model="form.codigoReserva"
              required
              :disabled="loading"
          />
          <small>Ingresa el código que recibiste al realizar tu compra</small>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="rut">
            <i class="fas fa-id-card"></i> RUT del Pasajero
          </label>
          <input
              type="text"
              id="rut"
              placeholder="12.345.678-9"
              v-model="form.rut"
              required
              :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="apellido">
            <i class="fas fa-user"></i> Apellido
          </label>
          <input
              type="text"
              id="apellido"
              placeholder="Tu apellido"
              v-model="form.apellido"
              required
              :disabled="loading"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group full-width">
          <label for="email">
            <i class="fas fa-envelope"></i> Email de Confirmación
          </label>
          <input
              type="email"
              id="email"
              placeholder="tucorreo@ejemplo.com"
              v-model="form.email"
              required
              :disabled="loading"
          />
          <small>Te enviaremos la confirmación a este correo</small>
        </div>
      </div>

      <!-- Mensaje de error -->
      <div v-if="error" class="error-message">
        <i class="fas fa-exclamation-circle"></i>
        {{ error }}
      </div>

      <!-- Mensaje de éxito -->
      <div v-if="success" class="success-message">
        <i class="fas fa-check-circle"></i>
        {{ success }}
      </div>

      <div class="info-box success">
        <i class="fas fa-lightbulb"></i>
        <div>
          <strong>Recuerda:</strong> Una vez confirmado tu pasaje, recibirás un código QR en tu correo electrónico. Preséntalo al momento de abordar el bus.
        </div>
      </div>

      <div class="checkbox-group">
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.aceptaTerminos" required :disabled="loading">
          <span>Acepto los <a href="about:blank" target="_blank">términos y condiciones</a></span>
        </label>
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.recibirNotificaciones" :disabled="loading">
          <span>Deseo recibir notificaciones sobre mi viaje</span>
        </label>
      </div>

      <button type="submit" class="btn-submit" :disabled="!form.aceptaTerminos || loading">
        <i class="fas fa-credit-card"></i> 
        {{ loading ? 'BUSCANDO...' : 'CONFIRMAR Y PAGAR' }}
      </button>
    </form>

    <!-- Detalles de la reserva confirmada -->
    <div v-if="currentBooking && paymentCompleted" class="booking-details">
      <h3><i class="fas fa-ticket-alt"></i> Detalles de la Reserva</h3>
      <div class="details-grid">
        <div class="detail-item">
          <span class="label">ID de Reserva:</span>
          <span class="value">{{ currentBooking.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Estado:</span>
          <span class="value status confirmed">
            Confirmado y Pagado
          </span>
        </div>
        <div class="detail-item">
          <span class="label">Asientos:</span>
          <span class="value">{{ currentBooking.seats.join(', ') }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Monto Total:</span>
          <span class="value price">{{ formatCurrency(currentBooking.totalAmount) }}</span>
        </div>
        <div class="detail-item" v-if="paymentTransactionId">
          <span class="label">Transacción:</span>
          <span class="value transaction">{{ paymentTransactionId }}</span>
        </div>
      </div>
    </div>

    <!-- Modal de Pago -->
    <PaymentModal
      :is-open="showPaymentModal"
      :booking="bookingForPayment"
      @close="closePaymentModal"
      @payment-complete="onPaymentComplete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useBookings } from '../composables/useBookings';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import type { BookingStatus, BookingResponse } from '../types/booking';
import PaymentModal from './PaymentModal.vue';
import "../assets/ConfirmarPasaje.css";

// Composable para gestionar reservas
const { currentBooking, loading, error, success, confirmBooking } = useBookings();

// Estado del modal de pago
const showPaymentModal = ref(false);
const paymentCompleted = ref(false);
const paymentTransactionId = ref<string | null>(null);

// Booking para el modal (puede ser simulado si no viene del backend)
const bookingForPayment = ref<BookingResponse>({
  id: 0,
  tripId: 0,
  userId: 0,
  seats: [],
  status: 'PENDING',
  totalAmount: 0,
  passengers: [],
  createdAt: '',
  expiresAt: '',
});

// Estado del formulario
const form = ref({
  codigoReserva: '',
  rut: '',
  apellido: '',
  email: '',
  aceptaTerminos: false,
  recibirNotificaciones: true,
});

/**
 * Confirmar pasaje - ahora abre el modal de pago
 */
const confirmarPasaje = async () => {
  if (!form.value.aceptaTerminos) {
    alert('Debes aceptar los términos y condiciones');
    return;
  }

  // Buscar o crear datos de booking para el pago
  // En un caso real, esto vendría del backend
  const bookingId = parseInt(form.value.codigoReserva) || Math.floor(Math.random() * 1000) + 1;
  
  bookingForPayment.value = {
    id: bookingId,
    tripId: 1,
    userId: 1,
    seats: ['A1', 'A2'],
    status: 'PENDING',
    totalAmount: 25000,
    passengers: [],
    createdAt: new Date().toISOString(),
    expiresAt: new Date(Date.now() + 3600000).toISOString(),
  };

  // Abrir modal de pago
  showPaymentModal.value = true;
};

/**
 * Cerrar modal de pago
 */
const closePaymentModal = () => {
  showPaymentModal.value = false;
};

/**
 * Manejar pago completado
 */
const onPaymentComplete = (transactionId: string) => {
  paymentCompleted.value = true;
  paymentTransactionId.value = transactionId;
  
  // Actualizar el booking local
  if (bookingForPayment.value) {
    currentBooking.value = {
      ...bookingForPayment.value,
      status: 'CONFIRMED',
    };
  }
  
  // Limpiar formulario
  form.value.codigoReserva = '';
  form.value.rut = '';
  form.value.apellido = '';
  form.value.aceptaTerminos = false;
};

/**
 * Obtener texto del estado
 */
const getStatusText = (status: BookingStatus): string => {
  const statusMap: Record<BookingStatus, string> = {
    'PENDING': 'Pendiente',
    'CONFIRMED': 'Confirmado',
    'CANCELLED': 'Cancelado',
  };
  return statusMap[status] || status;
};
</script>

<style scoped>
.error-message {
  background-color: rgba(244, 67, 54, 0.1);
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

.success-message {
  background-color: rgba(76, 175, 80, 0.1);
  border-left: 4px solid #4caf50;
  padding: 15px;
  margin: 20px 0;
  border-radius: 8px;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
}

.success-message i {
  color: #4caf50;
  font-size: 1.2rem;
}

.booking-details {
  margin-top: 30px;
  background: white;
  border-radius: 12px;
  padding: 25px;
  animation: fadeIn 0.5s ease-in;
}

.booking-details h3 {
  color: #0d286d;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item .label {
  color: #666;
  font-size: 0.85rem;
  font-weight: 600;
}

.detail-item .value {
  color: #333;
  font-size: 1.1rem;
  font-weight: 500;
}

.value.status {
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  display: inline-block;
  width: fit-content;
}

.value.status.pending {
  background-color: #ff9800;
  color: white;
}

.value.status.confirmed {
  background-color: #4caf50;
  color: white;
}

.value.status.cancelled {
  background-color: #f44336;
  color: white;
}

.value.price {
  color: #4caf50;
  font-weight: 700;
  font-size: 1.3rem;
}

.value.transaction {
  font-family: monospace;
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  color: #ffeb3b;
  padding: 5px 12px;
  border-radius: 6px;
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

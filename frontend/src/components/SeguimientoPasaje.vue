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
              :disabled="loading"
          />
        </div>

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
      </div>

      <button type="submit" class="btn-submit" :disabled="loading">
        <i class="fas fa-search"></i> 
        {{ loading ? 'BUSCANDO...' : 'BUSCAR PASAJE' }}
      </button>
    </form>

    <!-- Mensaje de error -->
    <div v-if="error" class="error-message">
      <i class="fas fa-exclamation-circle"></i>
      {{ error }}
    </div>

    <!-- Resultados de búsqueda -->
    <div v-if="currentBooking" class="resultado-container">
      <div class="resultado-header">
        <h3><i class="fas fa-check-circle"></i> Pasaje Encontrado</h3>
        <span class="estado-badge" :class="currentBooking.status.toLowerCase()">
          {{ getStatusText(currentBooking.status) }}
        </span>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="label"><i class="fas fa-ticket-alt"></i> Código</span>
          <span class="valor">{{ currentBooking.id }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-chair"></i> Asientos</span>
          <span class="valor">{{ currentBooking.seats.join(', ') }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-dollar-sign"></i> Precio</span>
          <span class="valor">{{ formatCurrency(currentBooking.totalAmount) }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-calendar-alt"></i> Fecha de Creación</span>
          <span class="valor">{{ formatDate(currentBooking.createdAt) }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-clock"></i> Hora de Creación</span>
          <span class="valor">{{ formatTime(currentBooking.createdAt) }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-hourglass-end"></i> Expira</span>
          <span class="valor">{{ formatDateTime(currentBooking.expiresAt) }}</span>
        </div>
      </div>

      <div class="timeline">
        <h4>Estado del Viaje</h4>
        <div class="timeline-item" :class="getTimelineClass('created')">
          <div class="timeline-icon"><i class="fas fa-check"></i></div>
          <div class="timeline-content">
            <h5>Reserva Creada</h5>
            <p>{{ formatDateTime(currentBooking.createdAt) }}</p>
          </div>
        </div>
        <div class="timeline-item" :class="getTimelineClass('confirmed')">
          <div class="timeline-icon"><i class="fas fa-credit-card"></i></div>
          <div class="timeline-content">
            <h5>Pago Procesado</h5>
            <p v-if="currentBooking.status === 'CONFIRMED'">Confirmado</p>
            <p v-else>Pendiente de confirmación</p>
          </div>
        </div>
        <div class="timeline-item" :class="getTimelineClass('ticket')">
          <div class="timeline-icon"><i class="fas fa-ticket-alt"></i></div>
          <div class="timeline-content">
            <h5>Pasaje Emitido</h5>
            <p v-if="currentBooking.status === 'CONFIRMED'">Puedes presentar tu código QR</p>
            <p v-else>Pendiente de confirmación</p>
          </div>
        </div>
        <div class="timeline-item pendiente">
          <div class="timeline-icon"><i class="fas fa-bus"></i></div>
          <div class="timeline-content">
            <h5>En Viaje</h5>
            <p>Próximamente</p>
          </div>
        </div>
      </div>

      <div class="acciones">
        <button class="btn-accion primary" @click="descargarPasaje">
          <i class="fas fa-qrcode"></i> Ver Código QR
        </button>
        <button class="btn-accion secondary" @click="descargarPasaje">
          <i class="fas fa-download"></i> Descargar Pasaje
        </button>
        <button class="btn-accion secondary" @click="reenviarEmail">
          <i class="fas fa-envelope"></i> Reenviar por Email
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useBookings } from '../composables/useBookings';
import { formatCurrency, formatDate, formatTime, formatDateTime } from '../utils/formatters';
import type { BookingStatus } from '../types/booking';
import "../assets/SeguimientoPasaje.css";

// Composable para gestionar reservas
const { currentBooking, loading, error, getUserBookings } = useBookings();

// Estado del formulario
const form = ref({
  codigoReserva: '',
  rut: '',
});

/**
 * Buscar pasaje
 * Nota: El backend actual usa userId, pero aquí simulamos búsqueda por código
 */
const buscarPasaje = async () => {
  // TODO: Cuando el backend tenga endpoint de búsqueda por código,
  // cambiar esto para usar ese endpoint
  // Por ahora, usamos getUserBookings con un userId de ejemplo
  await getUserBookings('123'); // ID temporal para pruebas
};

/**
 * Obtener texto del estado
 */
const getStatusText = (status: BookingStatus): string => {
  const statusMap: Record<BookingStatus, string> = {
    'PENDING': 'PENDIENTE',
    'CONFIRMED': 'CONFIRMADO',
    'CANCELLED': 'CANCELADO',
  };
  return statusMap[status] || status;
};

/**
 * Obtener clase CSS para timeline según el estado
 */
const getTimelineClass = (step: string): string => {
  if (!currentBooking.value) return 'pendiente';
  
  const status = currentBooking.value.status;
  
  if (step === 'created') return 'completado';
  if (step === 'confirmed') return status === 'CONFIRMED' ? 'completado' : 'pendiente';
  if (step === 'ticket') return status === 'CONFIRMED' ? 'activo' : 'pendiente';
  
  return 'pendiente';
};

/**
 * Descargar pasaje (placeholder)
 */
const descargarPasaje = () => {
  alert('Funcionalidad de descarga en desarrollo');
};

/**
 * Reenviar email (placeholder)
 */
const reenviarEmail = () => {
  alert('Email reenviado exitosamente');
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

.estado-badge.pending {
  background-color: #ff9800;
}

.estado-badge.confirmed {
  background-color: #4caf50;
}

.estado-badge.cancelled {
  background-color: #f44336;
}
</style>
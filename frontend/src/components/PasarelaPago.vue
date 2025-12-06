<template>
  <div class="payment-gateway">
    <div class="form-card">
      <div class="form-header">
        <i class="fas fa-credit-card"></i>
        <h2>Pasarela de Pago</h2>
        <p>Completa tu pago de forma segura</p>
      </div>

      <!-- Paso 1: Ingresar ID de Reserva -->
      <div v-if="!currentBooking" class="step-content">
        <form @submit.prevent="buscarReserva">
          <div class="form-group">
            <label for="bookingId">
              <i class="fas fa-ticket-alt"></i> ID de Reserva
            </label>
            <input
              type="number"
              id="bookingId"
              placeholder="Ej: 1"
              v-model="bookingIdInput"
              required
              :disabled="loadingBooking"
            />
            <small>Ingresa el ID de tu reserva para proceder al pago</small>
          </div>
          
          <button type="submit" class="btn-primary" :disabled="loadingBooking">
            <i class="fas fa-search"></i>
            {{ loadingBooking ? 'BUSCANDO...' : 'BUSCAR RESERVA' }}
          </button>
        </form>
      </div>

      <!-- Paso 2: Mostrar Detalle y Formulario de Pago -->
      <div v-else-if="!paymentComplete" class="step-content">
        <!-- Resumen de Reserva -->
        <div class="booking-summary">
          <h3><i class="fas fa-info-circle"></i> Detalle de tu Reserva</h3>
          <div class="summary-grid">
            <div class="summary-item">
              <span class="label">ID Reserva:</span>
              <span class="value">#{{ currentBooking.id }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Asientos:</span>
              <span class="value">{{ currentBooking.seats.join(', ') }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Estado:</span>
              <span class="value status" :class="currentBooking.status.toLowerCase()">
                {{ getStatusText(currentBooking.status) }}
              </span>
            </div>
            <div class="summary-item total">
              <span class="label">Total a Pagar:</span>
              <span class="value price">{{ formatCurrency(currentBooking.totalAmount) }}</span>
            </div>
          </div>
        </div>

        <!-- Formulario de Pago -->
        <form @submit.prevent="procesarPago" class="payment-form">
          <h3><i class="fas fa-wallet"></i> Método de Pago</h3>
          
          <!-- Selector de Método -->
          <div class="payment-methods">
            <label class="method-option" :class="{ active: selectedMethod === 'TARJETA_CREDITO' }">
              <input type="radio" v-model="selectedMethod" value="TARJETA_CREDITO" />
              <i class="fas fa-credit-card"></i>
              <span>Tarjeta de Crédito</span>
            </label>
            <label class="method-option" :class="{ active: selectedMethod === 'TARJETA_DEBITO' }">
              <input type="radio" v-model="selectedMethod" value="TARJETA_DEBITO" />
              <i class="fas fa-credit-card"></i>
              <span>Tarjeta de Débito</span>
            </label>
            <label class="method-option disabled" :class="{ active: selectedMethod === 'WEBPAY' }">
              <input type="radio" v-model="selectedMethod" value="WEBPAY" disabled />
              <i class="fas fa-globe"></i>
              <span>Webpay</span>
              <span class="coming-soon">Próximamente</span>
            </label>
            <label class="method-option disabled" :class="{ active: selectedMethod === 'MERCADOPAGO' }">
              <input type="radio" v-model="selectedMethod" value="MERCADOPAGO" disabled />
              <i class="fas fa-money-bill-wave"></i>
              <span>MercadoPago</span>
              <span class="coming-soon">Próximamente</span>
            </label>
          </div>

          <!-- Campos de Tarjeta (simulados) -->
          <div v-if="selectedMethod === 'TARJETA_CREDITO' || selectedMethod === 'TARJETA_DEBITO'" class="card-fields">
            <div class="form-group">
              <label><i class="fas fa-hashtag"></i> Número de Tarjeta</label>
              <input
                type="text"
                v-model="cardInfo.cardNumber"
                placeholder="1234 5678 9012 3456"
                maxlength="19"
                required
              />
            </div>
            <div class="form-row">
              <div class="form-group">
                <label><i class="fas fa-user"></i> Titular</label>
                <input
                  type="text"
                  v-model="cardInfo.cardHolder"
                  placeholder="NOMBRE APELLIDO"
                  required
                />
              </div>
            </div>
            
            <div v-if="selectedMethod === 'TARJETA_CREDITO'" class="form-row">
              <div class="form-group">
                <label><i class="fas fa-layers-group"></i> Cuotas</label>
                <select v-model="installments" class="select-input">
                  <option value="1">1 Cuota (Sin interés)</option>
                  <option value="3">3 Cuotas</option>
                  <option value="6">6 Cuotas</option>
                  <option value="12">12 Cuotas</option>
                </select>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group half">
                <label><i class="fas fa-calendar"></i> Vencimiento</label>
                <div class="expiry-inputs">
                  <input type="text" v-model="cardInfo.expiryMonth" placeholder="MM" maxlength="2" required />
                  <span>/</span>
                  <input type="text" v-model="cardInfo.expiryYear" placeholder="AA" maxlength="2" required />
                </div>
              </div>
              <div class="form-group half">
                <label><i class="fas fa-lock"></i> CVV</label>
                <input type="password" v-model="cardInfo.cvv" placeholder="***" maxlength="4" required />
              </div>
            </div>
          </div>

          <!-- Mensaje de error -->
          <div v-if="error" class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            {{ error }}
          </div>

          <!-- Botón de Pago -->
          <button type="submit" class="btn-pay" :disabled="loading || !selectedMethod">
            <i class="fas fa-lock"></i>
            {{ loading ? 'PROCESANDO PAGO...' : `PAGAR ${formatCurrency(currentBooking.totalAmount)}` }}
          </button>

          <div class="security-notice">
            <i class="fas fa-shield-alt"></i>
            <span>Pago 100% seguro. Tus datos están protegidos.</span>
          </div>
        </form>

        <button class="btn-back" @click="resetear">
          <i class="fas fa-arrow-left"></i> Volver
        </button>
      </div>

      <!-- Paso 3: Confirmación de Pago -->
      <div v-else class="step-content confirmation">
        <div class="success-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <h2>¡Pago Exitoso!</h2>
        <p class="success-message">Tu pago ha sido procesado correctamente</p>
        
        <div class="payment-details">
          <div class="detail-item">
            <span class="label">Número de Transacción:</span>
            <span class="value transaction-id">{{ currentPayment?.transactionId }}</span>
          </div>
          <div class="detail-item">
            <span class="label">Monto Pagado:</span>
            <span class="value price">{{ formatCurrency(currentPayment?.amount || 0) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">Método:</span>
            <span class="value">{{ getMethodText(currentPayment?.method) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">Estado:</span>
            <span class="value status completado">Completado</span>
          </div>
        </div>

        <div class="info-box success">
          <i class="fas fa-envelope"></i>
          <div>
            <strong>Confirmación enviada</strong>
            <p>Recibirás un correo electrónico con los detalles de tu compra y el código QR para abordar.</p>
          </div>
        </div>

        <button class="btn-primary" @click="resetear">
          <i class="fas fa-plus"></i> Realizar Otro Pago
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { usePayments } from '../composables/usePayments';
import { formatCurrency } from '../utils/formatters';
import type { PaymentMethod, CardInfo } from '../types/payment';
import type { BookingResponse, BookingStatus } from '../types/booking';
import { bookingService } from '../services/bookingService';
import { generateTicketPDF } from '../utils/ticketGenerator';

// Estado
const bookingIdInput = ref<number | null>(null);
const currentBooking = ref<BookingResponse | null>(null);
const loadingBooking = ref(false);
const selectedMethod = ref<PaymentMethod>('TARJETA_CREDITO');
const cardInfo = ref<CardInfo>({
  cardNumber: '',
  cardHolder: '',
  expiryMonth: '',
  expiryYear: '',
  cvv: '',
});
const installments = ref('1');
const { currentPayment, loading, error, processPayment, clearMessages } = usePayments();

const paymentComplete = computed(() => currentPayment.value?.status === 'APROBADO');

const buscarReserva = async () => {
  if (!bookingIdInput.value) return;
  
  loadingBooking.value = true;
  clearMessages();
  
  try {
    // Simular búsqueda - en producción llamar al API
    const response = await fetch(`http://localhost:8080/api/v1/bookings/${bookingIdInput.value}`);
    if (!response.ok) {
      throw new Error('Reserva no encontrada');
    }
    currentBooking.value = await response.json();
  } catch (err: any) {
    // Demo: Crear booking simulado para testing
    currentBooking.value = {
      id: bookingIdInput.value,
      tripId: 1,
      userId: 1,
      seats: ['A1', 'A2'],
      status: 'PENDING',
      totalAmount: 25000,
      passengers: [],
      createdAt: new Date().toISOString(),
      expiresAt: new Date(Date.now() + 3600000).toISOString(),
    };
  } finally {
    loadingBooking.value = false;
  }
};

// Procesar pago
const procesarPago = async () => {
  if (!currentBooking.value || !selectedMethod.value) return;
  
  await processPayment(currentBooking.value.id, selectedMethod.value);
  
  if (currentPayment.value?.status === 'APROBADO') {
      try {
          generateTicketPDF({
              ...currentBooking.value,
              status: 'CONFIRMED',
              paymentReference: currentPayment.value.transactionId
          });
      } catch (e) {
          console.error("Error PDF", e);
      }
  }
};

// Resetear
const resetear = () => {
  currentBooking.value = null;
  currentPayment.value = null;
  bookingIdInput.value = null;
  selectedMethod.value = 'TARJETA_CREDITO';
  installments.value = '1';
  cardInfo.value = {
    cardNumber: '',
    cardHolder: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
  };
  clearMessages();
};

// Helpers
const getStatusText = (status: BookingStatus): string => {
  const statusMap: Record<BookingStatus, string> = {
    'PENDING': 'Pendiente de Pago',
    'CONFIRMED': 'Confirmado',
    'CANCELLED': 'Cancelado',
  };
  return statusMap[status] || status;
};

const getMethodText = (method?: PaymentMethod): string => {
  if (!method) return '';
  const methodMap: Record<PaymentMethod, string> = {
    'TARJETA_CREDITO': 'Tarjeta de Crédito',
    'TARJETA_DEBITO': 'Tarjeta de Débito',
    'WEBPAY': 'Webpay',
    'MERCADOPAGO': 'MercadoPago',
    'BANK_TRANSFER': 'Transferencia Bancaria',
  };
  return methodMap[method] || method;
};
</script>

<style scoped>
.payment-gateway {
  padding: 20px 0;
}

.form-card {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  padding: 35px;
  width: 90%;
  max-width: 700px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.form-header i {
  font-size: 3rem;
  color: #ffeb3b;
  margin-bottom: 15px;
}

.form-header h2 {
  margin: 0 0 10px 0;
  font-size: 1.8rem;
}

.form-header p {
  opacity: 0.8;
  margin: 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-weight: 600;
  margin-bottom: 8px;
}

.form-group label i {
  color: #ffeb3b;
}

.form-group input {
  width: 100%;
  padding: 14px;
  border: 2px solid transparent;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #ffeb3b;
  box-shadow: 0 0 0 3px rgba(255, 235, 59, 0.2);
}

.select-input {
  width: 100%;
  padding: 14px;
  border: 2px solid transparent;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
  color: #333;
  cursor: pointer;
}

.select-input:focus {
  outline: none;
  border-color: #ffeb3b;
}

.form-group small {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
  margin-top: 5px;
  display: block;
}

.btn-primary, .btn-pay {
  width: 100%;
  padding: 16px;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #ffeb3b 0%, #ffc107 100%);
  color: #0d286d;
}

.btn-pay {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  color: white;
  margin-top: 20px;
}

.btn-primary:hover, .btn-pay:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.btn-primary:disabled, .btn-pay:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-back {
  width: 100%;
  padding: 12px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: transparent;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-back:hover {
  border-color: white;
  background: rgba(255, 255, 255, 0.1);
}

/* Booking Summary */
.booking-summary {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
}

.booking-summary h3 {
  color: #ffeb3b;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.summary-grid {
  display: grid;
  gap: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
}

.summary-item .label {
  color: rgba(255, 255, 255, 0.7);
}

.summary-item .value {
  color: white;
  font-weight: 600;
}

.summary-item.total {
  background: rgba(255, 235, 59, 0.2);
  border: 1px solid rgba(255, 235, 59, 0.3);
}

.summary-item .value.price {
  color: #ffeb3b;
  font-size: 1.3rem;
}

.summary-item .value.status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
}

.status.pending { background: #ff9800; }
.status.confirmed, .status.completado { background: #4caf50; }
.status.cancelled { background: #f44336; }

/* Payment Methods */
.payment-form h3 {
  color: white;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.payment-methods {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.method-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
}

.method-option input {
  display: none;
}

.method-option i {
  font-size: 1.5rem;
}

.method-option span {
  font-size: 0.85rem;
  text-align: center;
}

.method-option:hover {
  background: rgba(255, 255, 255, 0.15);
}

.method-option.active {
  border-color: #ffeb3b;
  background: rgba(255, 235, 59, 0.2);
}

.method-option.active i {
  color: #ffeb3b;
}

.method-option.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  filter: grayscale(1);
}

.method-option.disabled:hover {
  background: rgba(255, 255, 255, 0.1);
}

.coming-soon {
  font-size: 0.65rem;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 4px;
  margin-top: 4px;
}

.method-option.active i {
  color: #ffeb3b;
}

/* Card Fields */
.card-fields {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group.half {
  margin-bottom: 0;
}

.expiry-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
}

.expiry-inputs input {
  width: 60px;
  text-align: center;
}

.expiry-inputs span {
  color: white;
  font-size: 1.2rem;
}

/* Security Notice */
.security-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 15px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.security-notice i {
  color: #4caf50;
}

/* Error Message */
.error-message {
  background: rgba(244, 67, 54, 0.2);
  border-left: 4px solid #f44336;
  padding: 15px;
  border-radius: 8px;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 15px 0;
}

.error-message i {
  color: #f44336;
}

/* Confirmation */
.confirmation {
  text-align: center;
}

.success-icon {
  font-size: 5rem;
  color: #4caf50;
  margin-bottom: 20px;
  animation: scaleIn 0.5s ease-out;
}

.confirmation h2 {
  color: white;
  margin: 0 0 10px 0;
}

.confirmation .success-message {
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 30px;
}

.payment-details {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
  text-align: left;
}

.payment-details .detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.payment-details .detail-item:last-child {
  border-bottom: none;
}

.payment-details .label {
  color: rgba(255, 255, 255, 0.7);
}

.payment-details .value {
  color: white;
  font-weight: 600;
}

.transaction-id {
  font-family: monospace;
  background: rgba(255, 235, 59, 0.2);
  padding: 4px 10px;
  border-radius: 4px;
  color: #ffeb3b !important;
}

/* Info Box */
.info-box {
  display: flex;
  gap: 15px;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  text-align: left;
}

.info-box.success {
  background: rgba(76, 175, 80, 0.2);
  border: 1px solid rgba(76, 175, 80, 0.3);
}

.info-box i {
  font-size: 2rem;
  color: #4caf50;
}

.info-box strong {
  color: white;
  display: block;
  margin-bottom: 5px;
}

.info-box p {
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  font-size: 0.9rem;
}

@keyframes scaleIn {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* Responsive */
@media (max-width: 600px) {
  .form-card {
    padding: 25px 20px;
  }
  
  .payment-methods {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

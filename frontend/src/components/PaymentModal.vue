<template>
  <Teleport to="body">
    <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-container">
        <!-- Header con botón cerrar -->
        <div class="modal-header">
          <div class="header-content">
            <i class="fas fa-credit-card"></i>
            <div>
              <h2>{{ paymentComplete ? '¡Pago Exitoso!' : 'Procesar Pago' }}</h2>
              <p v-if="!paymentComplete">Completa tu pago de forma segura</p>
            </div>
          </div>
          <button class="close-btn" @click="closeModal" :disabled="loading">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <!-- Contenido del Modal -->
        <div class="modal-body">
          <!-- Vista: Formulario de Pago -->
          <div v-if="!paymentComplete" class="payment-content">
            <!-- Resumen de la reserva -->
            <div class="booking-summary">
              <h4><i class="fas fa-ticket-alt"></i> Resumen de tu Reserva</h4>
              <div class="summary-row">
                <span>ID Reserva:</span>
                <span class="value">#{{ booking.id }}</span>
              </div>
              <div class="summary-row">
                <span>Asientos:</span>
                <span class="value">{{ booking.seats?.join(', ') || 'N/A' }}</span>
              </div>
              <div class="summary-row total">
                <span>Total a Pagar:</span>
                <span class="value price">{{ formatCurrency(booking.totalAmount) }}</span>
              </div>
            </div>

            <!-- Selector de método de pago -->
            <div class="payment-methods">
              <h4><i class="fas fa-wallet"></i> Método de Pago</h4>
              <div class="methods-grid">
                <label class="method-card" :class="{ active: selectedMethod === 'TARJETA_CREDITO' }">
                  <input type="radio" v-model="selectedMethod" value="TARJETA_CREDITO" />
                  <i class="fas fa-credit-card"></i>
                  <span>Crédito</span>
                </label>
                <label class="method-card" :class="{ active: selectedMethod === 'TARJETA_DEBITO' }">
                  <input type="radio" v-model="selectedMethod" value="TARJETA_DEBITO" />
                  <i class="fas fa-credit-card"></i>
                  <span>Débito</span>
                </label>
                <label class="method-card" :class="{ active: selectedMethod === 'WEBPAY' }">
                  <input type="radio" v-model="selectedMethod" value="WEBPAY" />
                  <i class="fas fa-globe"></i>
                  <span>Webpay</span>
                </label>
                <label class="method-card" :class="{ active: selectedMethod === 'MERCADOPAGO' }">
                  <input type="radio" v-model="selectedMethod" value="MERCADOPAGO" />
                  <i class="fas fa-money-bill-wave"></i>
                  <span>MercadoPago</span>
                </label>
              </div>
            </div>

            <!-- Campos de tarjeta (simulados) -->
            <div v-if="isCardMethod" class="card-form">
              <div class="form-group">
                <label><i class="fas fa-hashtag"></i> Número de Tarjeta</label>
                <input type="text" v-model="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" />
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label><i class="fas fa-user"></i> Titular</label>
                  <input type="text" v-model="cardHolder" placeholder="NOMBRE APELLIDO" />
                </div>
              </div>
              <div class="form-row">
                <div class="form-group half">
                  <label><i class="fas fa-calendar"></i> Vencimiento</label>
                  <div class="expiry-row">
                    <input type="text" v-model="expiryMonth" placeholder="MM" maxlength="2" />
                    <span>/</span>
                    <input type="text" v-model="expiryYear" placeholder="AA" maxlength="2" />
                  </div>
                </div>
                <div class="form-group half">
                  <label><i class="fas fa-lock"></i> CVV</label>
                  <input type="password" v-model="cvv" placeholder="***" maxlength="4" />
                </div>
              </div>
            </div>

            <!-- Error -->
            <div v-if="error" class="error-message">
              <i class="fas fa-exclamation-circle"></i>
              {{ error }}
            </div>
          </div>

          <!-- Vista: Confirmación de Pago -->
          <div v-else class="success-content">
            <div class="success-icon">
              <i class="fas fa-check-circle"></i>
            </div>
            <p class="success-text">Tu pago ha sido procesado correctamente</p>
            
            <div class="payment-receipt">
              <div class="receipt-row">
                <span>Transacción:</span>
                <span class="value code">{{ currentPayment?.transactionId }}</span>
              </div>
              <div class="receipt-row">
                <span>Monto:</span>
                <span class="value">{{ formatCurrency(currentPayment?.amount || 0) }}</span>
              </div>
              <div class="receipt-row">
                <span>Estado:</span>
                <span class="value status">Completado</span>
              </div>
            </div>

            <div class="info-notice">
              <i class="fas fa-envelope"></i>
              <span>Recibirás un correo con los detalles y tu código QR para abordar.</span>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
          <button v-if="!paymentComplete" class="btn-cancel" @click="closeModal" :disabled="loading">
            Cancelar
          </button>
          <button 
            v-if="!paymentComplete" 
            class="btn-pay" 
            @click="handlePayment" 
            :disabled="loading || !selectedMethod"
          >
            <i :class="loading ? 'fas fa-spinner fa-spin' : 'fas fa-lock'"></i>
            {{ loading ? 'Procesando...' : `Pagar ${formatCurrency(booking.totalAmount)}` }}
          </button>
          <button v-else class="btn-done" @click="handleComplete">
            <i class="fas fa-check"></i>
            Listo
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { usePayments } from '../composables/usePayments';
import { formatCurrency } from '../utils/formatters';
import type { PaymentMethod } from '../types/payment';
import type { BookingResponse } from '../types/booking';

// Props
const props = defineProps<{
  isOpen: boolean;
  booking: BookingResponse;
}>();

// Emits
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'payment-complete', transactionId: string): void;
}>();

// State
const selectedMethod = ref<PaymentMethod>('TARJETA_CREDITO');
const cardNumber = ref('');
const cardHolder = ref('');
const expiryMonth = ref('');
const expiryYear = ref('');
const cvv = ref('');

// Composable
const { currentPayment, loading, error, processPayment, clearMessages } = usePayments();

// Computed
const isCardMethod = computed(() => 
  selectedMethod.value === 'TARJETA_CREDITO' || selectedMethod.value === 'TARJETA_DEBITO'
);

const paymentComplete = computed(() => currentPayment.value?.status === 'COMPLETADO');

// Watch para limpiar cuando se abre el modal
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    resetForm();
  }
});

// Methods
const resetForm = () => {
  selectedMethod.value = 'TARJETA_CREDITO';
  cardNumber.value = '';
  cardHolder.value = '';
  expiryMonth.value = '';
  expiryYear.value = '';
  cvv.value = '';
  currentPayment.value = null;
  clearMessages();
};

const closeModal = () => {
  if (!loading.value) {
    emit('close');
  }
};

const handlePayment = async () => {
  if (!props.booking?.id) return;
  
  await processPayment(props.booking.id, selectedMethod.value);
};

const handleComplete = () => {
  if (currentPayment.value?.transactionId) {
    emit('payment-complete', currentPayment.value.transactionId);
  }
  emit('close');
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}

.modal-container {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
}

.header-content i {
  font-size: 2rem;
  color: #ffeb3b;
}

.header-content h2 {
  margin: 0;
  font-size: 1.4rem;
}

.header-content p {
  margin: 5px 0 0 0;
  opacity: 0.7;
  font-size: 0.9rem;
}

.close-btn {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.close-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.modal-body {
  padding: 25px;
}

/* Booking Summary */
.booking-summary {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 20px;
}

.booking-summary h4 {
  color: #ffeb3b;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: rgba(255, 255, 255, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-row .value {
  color: white;
  font-weight: 600;
}

.summary-row.total {
  margin-top: 10px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 235, 59, 0.3);
}

.summary-row .value.price {
  color: #ffeb3b;
  font-size: 1.3rem;
}

/* Payment Methods */
.payment-methods h4 {
  color: white;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1rem;
}

.methods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.method-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
}

.method-card input {
  display: none;
}

.method-card i {
  font-size: 1.3rem;
}

.method-card span {
  font-size: 0.75rem;
  text-align: center;
}

.method-card:hover {
  background: rgba(255, 255, 255, 0.15);
}

.method-card.active {
  border-color: #ffeb3b;
  background: rgba(255, 235, 59, 0.15);
}

.method-card.active i {
  color: #ffeb3b;
}

/* Card Form */
.card-form {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.85rem;
  margin-bottom: 6px;
}

.form-group label i {
  color: #ffeb3b;
  font-size: 0.85rem;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.form-group input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.form-group input:focus {
  outline: none;
  border-color: #ffeb3b;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-group.half {
  margin-bottom: 0;
}

.expiry-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.expiry-row input {
  width: 50px;
  text-align: center;
}

.expiry-row span {
  color: white;
}

/* Error */
.error-message {
  background: rgba(244, 67, 54, 0.2);
  border-left: 3px solid #f44336;
  padding: 12px;
  border-radius: 6px;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 15px;
}

.error-message i {
  color: #f44336;
}

/* Success Content */
.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  font-size: 4rem;
  color: #4caf50;
  margin-bottom: 15px;
  animation: scaleIn 0.5s ease;
}

.success-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
  margin-bottom: 25px;
}

.payment-receipt {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 15px;
  text-align: left;
  margin-bottom: 20px;
}

.receipt-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  color: rgba(255, 255, 255, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.receipt-row:last-child {
  border-bottom: none;
}

.receipt-row .value {
  color: white;
  font-weight: 600;
}

.receipt-row .value.code {
  font-family: monospace;
  background: rgba(255, 235, 59, 0.2);
  padding: 3px 8px;
  border-radius: 4px;
  color: #ffeb3b;
}

.receipt-row .value.status {
  background: #4caf50;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
}

.info-notice {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(76, 175, 80, 0.15);
  border-radius: 8px;
  padding: 12px 15px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
}

.info-notice i {
  color: #4caf50;
  font-size: 1.2rem;
}

/* Footer */
.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 25px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-cancel {
  flex: 1;
  padding: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: transparent;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover:not(:disabled) {
  border-color: white;
  background: rgba(255, 255, 255, 0.1);
}

.btn-pay, .btn-done {
  flex: 2;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s;
}

.btn-pay {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  color: white;
}

.btn-done {
  background: linear-gradient(135deg, #ffeb3b 0%, #ffc107 100%);
  color: #0d286d;
  flex: 1;
}

.btn-pay:hover:not(:disabled), .btn-done:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.btn-pay:disabled, .btn-cancel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
@media (max-width: 500px) {
  .modal-container {
    width: 95%;
    margin: 10px;
  }
  
  .methods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

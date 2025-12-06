<template>
  <div class="form-card">
    <div class="form-header">
      <i class="fas fa-times-circle"></i>
      <h2>Anula tu Pasaje</h2>
      <p>Solicita la anulación de tu reserva</p>
    </div>

    <form @submit.prevent="anularPasaje">
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
          <small>Encuentra el código en tu correo de confirmación</small>
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
          <label for="email">
            <i class="fas fa-envelope"></i> Email
          </label>
          <input
              type="email"
              id="email"
              placeholder="tucorreo@ejemplo.com"
              v-model="form.email"
              required
              :disabled="loading"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group full-width">
          <label for="motivoAnulacion">
            <i class="fas fa-comment-alt"></i> Motivo de la Anulación
          </label>
          <select id="motivoAnulacion" v-model="form.motivoAnulacion" required :disabled="loading">
            <option value="">Selecciona un motivo</option>
            <option value="personal">Motivo Personal</option>
            <option value="salud">Motivo de Salud</option>
            <option value="laboral">Motivo Laboral</option>
            <option value="cambio_planes">Cambio de Planes</option>
            <option value="emergencia">Emergencia</option>
            <option value="otro">Otro</option>
          </select>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group full-width">
          <label for="detalles">
            <i class="fas fa-align-left"></i> Detalles Adicionales (Opcional)
          </label>
          <textarea
              id="detalles"
              placeholder="Cuéntanos más sobre tu solicitud..."
              v-model="form.detalles"
              rows="4"
              :disabled="loading"
          ></textarea>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="metodoDevolución">
            <i class="fas fa-credit-card"></i> Método de Devolución
          </label>
          <select id="metodoDevolución" v-model="form.metodoDevolución" required :disabled="loading">
            <option value="">Selecciona método</option>
            <option value="original">Método de pago original</option>
            <option value="transferencia">Transferencia bancaria</option>
            <option value="vale">Vale para futura compra</option>
          </select>
        </div>

        <div class="form-group" v-if="form.metodoDevolución === 'transferencia'">
          <label for="cuentaBancaria">
            <i class="fas fa-university"></i> Número de Cuenta
          </label>
          <input
              type="text"
              id="cuentaBancaria"
              placeholder="123456789"
              v-model="form.cuentaBancaria"
              :disabled="loading"
          />
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

      <div class="warning-box">
        <i class="fas fa-exclamation-triangle"></i>
        <div>
          <strong>Políticas de Anulación:</strong>
          <ul>
            <li>Anulaciones con más de 24 horas: Devolución del 80%</li>
            <li>Anulaciones entre 12-24 horas: Devolución del 50%</li>
            <li>Anulaciones con menos de 12 horas: Sin devolución</li>
          </ul>
          <p style="margin-top: 10px;">El proceso de reembolso puede tardar de 5 a 10 días hábiles.</p>
        </div>
      </div>

      <div class="checkbox-group">
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.aceptaPoliticas" required :disabled="loading">
          <span>He leído y acepto las políticas de anulación</span>
        </label>
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.confirmaAnulacion" required :disabled="loading">
          <span>Confirmo que deseo anular definitivamente este pasaje</span>
        </label>
      </div>

      <button type="submit" class="btn-submit" :disabled="!form.aceptaPoliticas || !form.confirmaAnulacion || loading">
        <i class="fas fa-times-circle"></i> 
        {{ loading ? 'ANULANDO...' : 'ANULAR PASAJE' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useBookings } from '../composables/useBookings';
import "../assets/AnularPasaje.css";

// Composable para gestionar reservas
const { loading, error, success, cancelBooking } = useBookings();

// Estado del formulario
const form = ref({
  codigoReserva: '',
  rut: '',
  email: '',
  motivoAnulacion: '',
  detalles: '',
  metodoDevolución: '',
  cuentaBancaria: '',
  aceptaPoliticas: false,
  confirmaAnulacion: false,
});

/**
 * Anular pasaje
 */
const anularPasaje = async () => {
  if (!form.value.aceptaPoliticas || !form.value.confirmaAnulacion) {
    alert('Debes aceptar las políticas y confirmar la anulación');
    return;
  }

  const resultado = await cancelBooking(form.value.codigoReserva);
  
  if (resultado) {
    // Limpiar formulario después de anular
    form.value = {
      codigoReserva: '',
      rut: '',
      email: '',
      motivoAnulacion: '',
      detalles: '',
      metodoDevolución: '',
      cuentaBancaria: '',
      aceptaPoliticas: false,
      confirmaAnulacion: false,
    };
  }
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
  animation: fadeIn 0.5s ease-in;
}

.success-message i {
  color: #4caf50;
  font-size: 1.2rem;
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
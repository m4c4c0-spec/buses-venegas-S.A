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
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group full-width">
          <label for="motivoAnulacion">
            <i class="fas fa-comment-alt"></i> Motivo de la Anulación
          </label>
          <select id="motivoAnulacion" v-model="form.motivoAnulacion" required>
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
          ></textarea>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="metodoDevolución">
            <i class="fas fa-credit-card"></i> Método de Devolución
          </label>
          <select id="metodoDevolución" v-model="form.metodoDevolución" required>
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
          />
        </div>
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
          <input type="checkbox" v-model="form.aceptaPoliticas" required>
          <span>He leído y acepto las políticas de anulación</span>
        </label>
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.confirmaAnulacion" required>
          <span>Confirmo que deseo anular definitivamente este pasaje</span>
        </label>
      </div>

      <button type="submit" class="btn-submit" :disabled="!form.aceptaPoliticas || !form.confirmaAnulacion">
        <i class="fas fa-times-circle"></i> ANULAR PASAJE
      </button>
    </form>
  </div>
</template>

<script>
export default {
  name: "AnulaPasaje",
  data() {
    return {
      form: {
        codigoReserva: "",
        rut: "",
        email: "",
        motivoAnulacion: "",
        detalles: "",
        metodoDevolución: "",
        cuentaBancaria: "",
        aceptaPoliticas: false,
        confirmaAnulacion: false,
      },
    };
  },
  methods: {
    anularPasaje() {
      console.log("Anulando pasaje:", this.form);
      alert(`Solicitud de anulación recibida para la reserva ${this.form.codigoReserva}`);
      // Aquí se conectará con el backend
    },
  },
};
</script>

<style scoped>
.form-card {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  padding: 40px;
  width: 90%;
  max-width: 900px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  top: -30px;
}

.form-header {
  text-align: center;
  color: white;
  margin-bottom: 35px;
}

.form-header i {
  font-size: 3rem;
  color: #f44336;
  margin-bottom: 15px;
}

.form-header h2 {
  font-size: 2rem;
  margin-bottom: 10px;
}

.form-header p {
  opacity: 0.9;
  font-size: 1.1rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.full-width {
  grid-column: 1 / -1;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  margin-bottom: 10px;
  color: white;
  font-size: 0.9rem;
}

label i {
  color: #f44336;
}

input, select, textarea {
  width: 100%;
  padding: 14px;
  border: 2px solid transparent;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 0.95rem;
  font-family: "Poppins", sans-serif;
  transition: all 0.3s;
  background-color: white;
}

input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: #f44336;
  box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.2);
}

textarea {
  resize: vertical;
  min-height: 100px;
}

small {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.8rem;
  margin-top: 5px;
}

.warning-box {
  background-color: rgba(244, 67, 54, 0.1);
  border-left: 4px solid #f44336;
  padding: 15px;
  margin: 20px 0;
  border-radius: 8px;
  display: flex;
  gap: 15px;
  color: white;
}

.warning-box i {
  color: #f44336;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.warning-box ul {
  margin: 10px 0;
  padding-left: 20px;
}

.warning-box li {
  margin: 5px 0;
}

.checkbox-group {
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.checkbox-container {
  display: flex;
  align-items: center;
  color: white;
  cursor: pointer;
  user-select: none;
}

.checkbox-container input[type="checkbox"] {
  width: 18px;
  height: 18px;
  margin-right: 10px;
  cursor: pointer;
}

.checkbox-container span {
  font-size: 0.9rem;
}

.btn-submit {
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
  border: none;
  padding: 16px 40px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  font-size: 1.1rem;
  width: 100%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(244, 67, 54, 0.3);
  margin-top: 10px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(244, 67, 54, 0.4);
}

.btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .form-card {
    padding: 30px 20px;
  }

  .form-header h2 {
    font-size: 1.5rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
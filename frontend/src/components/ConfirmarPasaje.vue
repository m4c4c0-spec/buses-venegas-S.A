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
          />
          <small>Te enviaremos la confirmación a este correo</small>
        </div>
      </div>

      <div class="info-box success">
        <i class="fas fa-lightbulb"></i>
        <div>
          <strong>Recuerda:</strong> Una vez confirmado tu pasaje, recibirás un código QR en tu correo electrónico. Preséntalo al momento de abordar el bus.
        </div>
      </div>

      <div class="checkbox-group">
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.aceptaTerminos" required>
          <span>Acepto los <a href="about:blank" target="_blank">términos y condiciones</a></span>
        </label>
        <label class="checkbox-container">
          <input type="checkbox" v-model="form.recibirNotificaciones">
          <span>Deseo recibir notificaciones sobre mi viaje</span>
        </label>
      </div>

      <button type="submit" class="btn-submit" :disabled="!form.aceptaTerminos">
        <i class="fas fa-check-circle"></i> CONFIRMAR PASAJE
      </button>
    </form>
  </div>
</template>

<script>
export default {
  name: "ConfirmaPasaje",
  data() {
    return {
      form: {
        codigoReserva: "",
        rut: "",
        apellido: "",
        email: "",
        aceptaTerminos: false,
        recibirNotificaciones: true,
      },
    };
  },
  methods: {
    confirmarPasaje() {
      console.log("Confirmando pasaje:", this.form);
      alert(`¡Pasaje confirmado! Código: ${this.form.codigoReserva}`);
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
  color: #4caf50;
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
  color: #4caf50;
}

input {
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

input:focus {
  outline: none;
  border-color: #4caf50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.2);
}

small {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.8rem;
  margin-top: 5px;
}

.info-box {
  background-color: rgba(76, 175, 80, 0.1);
  border-left: 4px solid #4caf50;
  padding: 15px;
  margin: 20px 0;
  border-radius: 8px;
  display: flex;
  gap: 15px;
  color: white;
}

.info-box i {
  color: #4caf50;
  font-size: 1.5rem;
  flex-shrink: 0;
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

.checkbox-container a {
  color: #4caf50;
  text-decoration: underline;
}

.btn-submit {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
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
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
  margin-top: 10px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
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
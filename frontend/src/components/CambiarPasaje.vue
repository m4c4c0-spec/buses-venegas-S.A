<template>
  <div class="form-card">
    <div class="form-header">
      <i class="fas fa-exchange-alt"></i>
      <h2>Cambia tu Pasaje</h2>
      <p>Modifica la fecha o el horario de tu viaje</p>
    </div>

    <form @submit.prevent="cambiarPasaje">
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
        <div class="form-group">
          <label for="nuevaFecha">
            <i class="fas fa-calendar-alt"></i> Nueva Fecha
          </label>
          <input
              type="date"
              id="nuevaFecha"
              v-model="form.nuevaFecha"
              :min="fechaMinima"
              required
          />
        </div>

        <div class="form-group">
          <label for="motivoCambio">
            <i class="fas fa-comment-alt"></i> Motivo del Cambio
          </label>
          <select id="motivoCambio" v-model="form.motivoCambio" required>
            <option value="">Selecciona un motivo</option>
            <option value="personal">Motivo Personal</option>
            <option value="laboral">Motivo Laboral</option>
            <option value="salud">Motivo de Salud</option>
            <option value="otro">Otro</option>
          </select>
        </div>
      </div>

      <div class="info-box">
        <i class="fas fa-info-circle"></i>
        <div>
          <strong>Importante:</strong> Los cambios de pasaje pueden tener un costo adicional según las políticas de la empresa. Revisa los términos y condiciones antes de confirmar.
        </div>
      </div>

      <button type="submit" class="btn-submit">
        <i class="fas fa-exchange-alt"></i> SOLICITAR CAMBIO
      </button>
    </form>
  </div>
</template>

<script>
export default {
  name: "CambiaPasaje",
  data() {
    return {
      form: {
        codigoReserva: "",
        rut: "",
        email: "",
        nuevaFecha: "",
        motivoCambio: "",
      },
      fechaMinima: new Date().toISOString().split('T')[0]
    };
  },
  methods: {
    cambiarPasaje() {
      console.log("Solicitando cambio:", this.form);
      alert(`Cambio solicitado para la reserva ${this.form.codigoReserva}`);
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
  color: #ffeb3b;
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
  color: #ffeb3b;
}

input, select {
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

input:focus, select:focus {
  outline: none;
  border-color: #ffeb3b;
  box-shadow: 0 0 0 3px rgba(255, 235, 59, 0.2);
}

small {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.8rem;
  margin-top: 5px;
}

.info-box {
  background-color: rgba(255, 235, 59, 0.1);
  border-left: 4px solid #ffeb3b;
  padding: 15px;
  margin: 20px 0;
  border-radius: 8px;
  display: flex;
  gap: 15px;
  color: white;
}

.info-box i {
  color: #ffeb3b;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.btn-submit {
  background: linear-gradient(135deg, #ffeb3b 0%, #ffc107 100%);
  color: #0d286d;
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
  box-shadow: 0 4px 15px rgba(255, 235, 59, 0.3);
  margin-top: 10px;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 235, 59, 0.4);
}

.btn-submit:active {
  transform: translateY(0);
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

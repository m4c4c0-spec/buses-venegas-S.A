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
import "../assets/Cambiarpasaje.css";

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


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
          />
        </div>
      </div>

      <button type="submit" class="btn-submit">
        <i class="fas fa-search"></i> BUSCAR PASAJE
      </button>
    </form>

    <div v-if="mostrarResultado" class="resultado-container">
      <div class="resultado-header">
        <h3><i class="fas fa-check-circle"></i> Pasaje Encontrado</h3>
        <span class="estado-badge confirmado">CONFIRMADO</span>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="label"><i class="fas fa-user"></i> Pasajero</span>
          <span class="valor">Juan Pérez González</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-ticket-alt"></i> Código</span>
          <span class="valor">{{ form.codigoReserva }}</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-map-marker-alt"></i> Origen</span>
          <span class="valor">Santiago</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-map-marker-alt"></i> Destino</span>
          <span class="valor">Concepción</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-calendar-alt"></i> Fecha</span>
          <span class="valor">15 de Octubre, 2025</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-clock"></i> Hora</span>
          <span class="valor">14:30</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-chair"></i> Asiento</span>
          <span class="valor">12A</span>
        </div>
        <div class="info-item">
          <span class="label"><i class="fas fa-dollar-sign"></i> Precio</span>
          <span class="valor">$12,500</span>
        </div>
      </div>

      <div class="timeline">
        <h4>Estado del Viaje</h4>
        <div class="timeline-item completado">
          <div class="timeline-icon"><i class="fas fa-check"></i></div>
          <div class="timeline-content">
            <h5>Reserva Confirmada</h5>
            <p>12 de Octubre, 2025 - 10:30 AM</p>
          </div>
        </div>
        <div class="timeline-item completado">
          <div class="timeline-icon"><i class="fas fa-credit-card"></i></div>
          <div class="timeline-content">
            <h5>Pago Procesado</h5>
            <p>12 de Octubre, 2025 - 10:31 AM</p>
          </div>
        </div>
        <div class="timeline-item activo">
          <div class="timeline-icon"><i class="fas fa-ticket-alt"></i></div>
          <div class="timeline-content">
            <h5>Pasaje Emitido</h5>
            <p>Puedes presentar tu código QR</p>
          </div>
        </div>
        <div class="timeline-item pendiente">
          <div class="timeline-icon"><i class="fas fa-bus"></i></div>
          <div class="timeline-content">
            <h5>En Viaje</h5>
            <p>15 de Octubre, 2025 - 14:30</p>
          </div>
        </div>
      </div>

      <div class="acciones">
        <button class="btn-accion primary">
          <i class="fas fa-qrcode"></i> Ver Código QR
        </button>
        <button class="btn-accion secondary">
          <i class="fas fa-download"></i> Descargar Pasaje
        </button>
        <button class="btn-accion secondary">
          <i class="fas fa-envelope"></i> Reenviar por Email
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "SeguimientoPasaje",
  data() {
    return {
      form: {
        codigoReserva: "",
        rut: "",
      },
      mostrarResultado: false,
    };
  },
  methods: {
    buscarPasaje() {
      console.log("Buscando pasaje:", this.form);
      this.mostrarResultado = true;
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
  color: #2196f3;
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
  color: #2196f3;
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
  border-color: #2196f3;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.2);
}

.btn-submit {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
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
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.4);
}

.btn-submit:active {
  transform: translateY(0);
}

.resultado-container {
  margin-top: 30px;
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  animation: fadeIn 0.5s ease-in;
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

.resultado-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.resultado-header h3 {
  color: #0d286d;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.resultado-header h3 i {
  color: #4caf50;
}

.estado-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.85rem;
}

.estado-badge.confirmado {
  background-color: #4caf50;
  color: white;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  color: #666;
  font-size: 0.85rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label i {
  color: #2196f3;
}

.info-item .valor {
  color: #333;
  font-size: 1.1rem;
  font-weight: 500;
}

.timeline {
  margin: 30px 0;
}

.timeline h4 {
  color: #0d286d;
  font-size: 1.3rem;
  margin-bottom: 20px;
}

.timeline-item {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  position: relative;
}

.timeline-item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 17px;
  top: 40px;
  width: 2px;
  height: calc(100% + 5px);
  background-color: #e0e0e0;
}

.timeline-icon {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background-color: #e0e0e0;
  color: white;
}

.timeline-item.completado .timeline-icon {
  background-color: #4caf50;
}

.timeline-item.activo .timeline-icon {
  background-color: #2196f3;
  box-shadow: 0 0 0 4px rgba(33, 150, 243, 0.2);
}

.timeline-item.pendiente .timeline-icon {
  background-color: #e0e0e0;
}

.timeline-content {
  flex: 1;
}

.timeline-content h5 {
  color: #333;
  margin-bottom: 5px;
  font-size: 1rem;
}

.timeline-content p {
  color: #666;
  font-size: 0.85rem;
}

.acciones {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 30px;
}

.btn-accion {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-accion.primary {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  color: white;
  box-shadow: 0 4px 10px rgba(33, 150, 243, 0.3);
}

.btn-accion.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(33, 150, 243, 0.4);
}

.btn-accion.secondary {
  background-color: #f5f5f5;
  color: #0d286d;
  border: 2px solid #e0e0e0;
}

.btn-accion.secondary:hover {
  background-color: #e0e0e0;
  transform: translateY(-2px);
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

  .resultado-container {
    padding: 20px;
  }

  .resultado-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .acciones {
    grid-template-columns: 1fr;
  }
}
</style>

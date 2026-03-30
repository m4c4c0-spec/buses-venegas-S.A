<template>
  <div class="exito-container">
    <div class="icon-success">
      <i class="fas fa-check-circle"></i>
    </div>
    <h2>¡Pago Exitoso!</h2>
    <p class="subtitulo">Tu reserva ha sido confirmada correctamente.</p>
    
    <!-- Outbound Trip -->
    <div class="ticket-card">
      <div class="ticket-header">
        <h3>Buses Venegas S.A.</h3>
        <span class="order-id">Reserva #{{ numReserva }}</span>
      </div>
      <div class="ticket-label" v-if="detallesReserva.idaYVuelta">🚌 Viaje de Ida</div>
      <div class="ticket-body">
        <div class="route-info">
          <div class="punto">
            <span>Origen</span>
            <strong>{{ detallesReserva.origen }}</strong>
          </div>
          <i class="fas fa-long-arrow-alt-right arrow"></i>
          <div class="punto">
            <span>Destino</span>
            <strong>{{ detallesReserva.destino }}</strong>
          </div>
        </div>
        <div class="details-grid">
          <div class="detail-item">
            <span>Fecha</span>
            <strong>{{ detallesReserva.fechaIda }}</strong>
          </div>
          <div class="detail-item">
            <span>Pasajeros</span>
            <strong>{{ detallesReserva.pasajeros }}</strong>
          </div>
          <div class="detail-item">
            <span>Asientos</span>
            <strong>{{ detallesReserva.asientos.join(', ') }}</strong>
          </div>
          <div class="detail-item">
            <span>Pagado</span>
            <strong>${{ detallesReserva.precioTotal.toLocaleString() }}</strong>
          </div>
        </div>
      </div>
    </div>

    <!-- Return Trip (if round-trip) -->
    <div class="ticket-card" v-if="detallesReserva.idaYVuelta && detallesReserva.vuelta">
      <div class="ticket-label vuelta">🔄 Viaje de Vuelta</div>
      <div class="ticket-body">
        <div class="route-info">
          <div class="punto">
            <span>Origen</span>
            <strong>{{ detallesReserva.destino }}</strong>
          </div>
          <i class="fas fa-long-arrow-alt-right arrow"></i>
          <div class="punto">
            <span>Destino</span>
            <strong>{{ detallesReserva.origen }}</strong>
          </div>
        </div>
        <div class="details-grid">
          <div class="detail-item">
            <span>Fecha</span>
            <strong>{{ detallesReserva.fechaVuelta }}</strong>
          </div>
          <div class="detail-item">
            <span>Asientos</span>
            <strong>{{ detallesReserva.vuelta.asientos.join(', ') }}</strong>
          </div>
        </div>
      </div>
    </div>
    
    <div class="info-email">
      <i class="fas fa-envelope-open-text"></i>
      <div class="text">
        <h4>Boleta y Pasaje Enviados</h4>
        <p>Hemos enviado una confirmación formal y tu boleta electrónica al correo de los pasajeros.</p>
      </div>
    </div>

    <button @click="$emit('volver-inicio')" class="btn-inicio">Volver al Inicio</button>
  </div>
</template>

<script>
export default {
  name: 'ComprobanteExito',
  props: {
    detallesReserva: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      numReserva: this.detallesReserva?.idReserva || Math.floor(Math.random() * 1000000).toString().padStart(6, '0')
    }
  }
}
</script>

<style scoped>
.exito-container {
  background: white;
  padding: 50px 40px;
  width: 90%;
  max-width: 600px;
  margin: -30px auto 40px auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
  position: relative;
  z-index: 10;
  animation: slideUp 0.5s ease;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.icon-success {
  font-size: 5rem;
  color: #28a745;
  margin-bottom: 20px;
  animation: bounce 1s ease;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-30px); }
  60% { transform: translateY(-15px); }
}

h2 {
  color: #0d286d;
  font-size: 2rem;
  margin-bottom: 5px;
}

.subtitulo {
  color: #666;
  margin-bottom: 40px;
  font-size: 1.1rem;
}

.ticket-card {
  background: white;
  border: 2px dashed #ced4da;
  border-radius: 12px;
  text-align: left;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
}

.ticket-label {
  background: #0d286d;
  color: white;
  padding: 8px 20px;
  font-weight: 600;
  font-size: 0.95rem;
}

.ticket-label.vuelta {
  background: #174291;
}

.ticket-header {
  background: #f8f9fa;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px dashed #ced4da;
}

.ticket-header h3 {
  color: #0d286d;
  margin: 0;
  font-size: 1.2rem;
}

.order-id {
  color: #6c757d;
  font-weight: bold;
}

.ticket-body {
  padding: 20px;
}

.route-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  background: #f0f4f8;
  padding: 15px;
  border-radius: 8px;
}

.punto {
  display: flex;
  flex-direction: column;
}

.punto span {
  font-size: 0.85rem;
  color: #6c757d;
  text-transform: uppercase;
}

.punto strong {
  font-size: 1.3rem;
  color: #0d286d;
}

.arrow {
  font-size: 1.5rem;
  color: #adb5bd;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-item span {
  font-size: 0.85rem;
  color: #6c757d;
}

.detail-item strong {
  font-size: 1.1rem;
  color: #343a40;
}

.info-email {
  display: flex;
  align-items: center;
  background: #e3f2fd;
  color: #0d47a1;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 40px;
  text-align: left;
  gap: 20px;
}

.info-email i {
  font-size: 2.5rem;
}

.info-email h4 {
  margin: 0 0 5px 0;
}

.info-email p {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.btn-inicio {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  color: white;
  border: none;
  padding: 16px 40px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
}

.btn-inicio:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(13, 40, 109, 0.3);
}
</style>

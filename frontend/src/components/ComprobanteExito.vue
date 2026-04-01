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
    
    <div class="info-pdf">
      <i class="fas fa-file-pdf"></i>
      <div class="text">
        <h4>Descarga tu Boleto</h4>
        <p>Descarga el comprobante en PDF con los datos de todos los pasajeros y preséntalo al abordar.</p>
      </div>
    </div>

    <button @click="descargarPDF" class="btn-pdf">
      <i class="fas fa-download"></i> Descargar Boleto PDF
    </button>

    <button @click="$emit('volver-inicio')" class="btn-inicio">Volver al Inicio</button>
  </div>
</template>

<script>
import jsPDF from 'jspdf';

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
  },
  methods: {
    descargarPDF() {
      const d = this.detallesReserva;
      const doc = new jsPDF();
      const pw = doc.internal.pageSize.width;
      const pasajeros = d.pasajerosData || [];

      // === PAGINA 1: Resumen de compra ===
      // Header azul
      doc.setFillColor(13, 40, 109);
      doc.rect(0, 0, pw, 38, 'F');
      doc.setTextColor(255, 255, 255);
      doc.setFontSize(20);
      doc.setFont('helvetica', 'bold');
      doc.text('Buses Venegas S.A.', 15, 18);
      doc.setFontSize(11);
      doc.setFont('helvetica', 'normal');
      doc.text('Comprobante de Reserva', 15, 28);
      doc.text('Reserva #' + this.numReserva, pw - 65, 18);

      // Datos del viaje
      doc.setTextColor(0, 0, 0);
      doc.setFontSize(13);
      doc.setFont('helvetica', 'bold');
      doc.text('Detalles del Viaje', 15, 52);

      doc.setFontSize(10);
      doc.setFont('helvetica', 'normal');
      const horaSalida = d.horarioViaje ? (d.horarioViaje.horaSalida || d.horarioViaje.salida || '') : '';
      const filas = [
        ['Origen', d.origen || ''],
        ['Destino', d.destino || ''],
        ['Fecha', d.fechaIda || ''],
        ['Horario', horaSalida],
        ['Asientos', (d.asientos || []).join(', ')],
        ['Total Pagado', '$' + (d.precioTotal || 0).toLocaleString() + ' CLP']
      ];

      let y = 60;
      filas.forEach(([label, valor]) => {
        doc.setFont('helvetica', 'bold');
        doc.text(label + ':', 15, y);
        doc.setFont('helvetica', 'normal');
        doc.text(String(valor), 65, y);
        y += 8;
      });

      // Tabla de pasajeros
      y += 8;
      doc.setFontSize(13);
      doc.setFont('helvetica', 'bold');
      doc.text('Pasajeros', 15, y);
      y += 8;

      // Encabezado tabla
      doc.setFillColor(44, 82, 130);
      doc.rect(15, y - 5, pw - 30, 8, 'F');
      doc.setTextColor(255, 255, 255);
      doc.setFontSize(9);
      doc.setFont('helvetica', 'bold');
      doc.text('Asiento', 17, y);
      doc.text('Nombre', 40, y);
      doc.text('RUT', 110, y);
      doc.text('Email', 145, y);
      y += 6;

      // Filas de pasajeros
      doc.setTextColor(0, 0, 0);
      doc.setFont('helvetica', 'normal');
      pasajeros.forEach((p) => {
        doc.setFillColor(y % 12 < 6 ? 245 : 255, y % 12 < 6 ? 245 : 255, y % 12 < 6 ? 245 : 255);
        doc.rect(15, y - 4, pw - 30, 7, 'F');
        doc.text(String(p.asiento || ''), 17, y);
        doc.text((p.nombre || '') + ' ' + (p.apellidos || ''), 40, y);
        doc.text(p.rut || '', 110, y);
        doc.text(p.email || '', 145, y);
        y += 7;
      });

      // Pie de pagina 1
      doc.setFontSize(8);
      doc.setTextColor(120, 120, 120);
      doc.text('Este documento es su comprobante de compra. Presentar al abordar.', 15, 280);

      // === PAGINAS SIGUIENTES: Tarjeta de embarque por pasajero ===
      pasajeros.forEach((p) => {
        doc.addPage();

        // Header
        doc.setFillColor(13, 40, 109);
        doc.rect(0, 0, pw, 30, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(18);
        doc.setFont('helvetica', 'bold');
        doc.text('TARJETA DE EMBARQUE', 15, 20);
        doc.setFontSize(9);
        doc.text('Buses Venegas S.A.', pw - 55, 20);

        // Circulo con asiento
        doc.setFillColor(255, 235, 59);
        doc.circle(pw - 30, 50, 14, 'F');
        doc.setTextColor(13, 40, 109);
        doc.setFontSize(16);
        doc.setFont('helvetica', 'bold');
        const asientoTxt = String(p.asiento || '?');
        doc.text(asientoTxt, pw - 30 - (doc.getTextWidth(asientoTxt) / 2), 54);
        doc.setFontSize(7);
        doc.text('ASIENTO', pw - 38, 63);

        // Nombre pasajero
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        doc.text('PASAJERO', 15, 48);
        doc.setFontSize(20);
        doc.setFont('helvetica', 'bold');
        doc.text(((p.nombre || '') + ' ' + (p.apellidos || '')).toUpperCase(), 15, 60);

        // Linea separadora
        doc.setDrawColor(200, 200, 200);
        doc.line(15, 70, pw - 15, 70);

        // Info del viaje
        doc.setFontSize(9);
        doc.setTextColor(120, 120, 120);
        doc.text('ORIGEN', 15, 82);
        doc.text('DESTINO', 80, 82);
        doc.text('FECHA', 145, 82);

        doc.setFontSize(14);
        doc.setTextColor(0, 0, 0);
        doc.setFont('helvetica', 'bold');
        doc.text(d.origen || '', 15, 92);
        doc.text(d.destino || '', 80, 92);
        doc.text(d.fechaIda || '', 145, 92);

        doc.setFontSize(9);
        doc.setTextColor(120, 120, 120);
        doc.setFont('helvetica', 'normal');
        doc.text('HORA', 15, 106);
        doc.text('RUT', 80, 106);
        doc.text('RESERVA', 145, 106);

        doc.setFontSize(14);
        doc.setTextColor(0, 0, 0);
        doc.setFont('helvetica', 'bold');
        doc.text(horaSalida || 'Ver boleto', 15, 116);
        doc.text(p.rut || '', 80, 116);
        doc.text('#' + this.numReserva, 145, 116);

        // Cuadro de instrucciones
        doc.setFillColor(255, 243, 205);
        doc.rect(15, 130, pw - 30, 20, 'F');
        doc.setFontSize(9);
        doc.setTextColor(133, 100, 4);
        doc.setFont('helvetica', 'normal');
        doc.text('Presentar este documento (impreso o digital) junto con su', 20, 140);
        doc.text('documento de identidad al momento de abordar el bus.', 20, 146);
      });

      doc.save('boletos_buses_venegas_' + this.numReserva + '.pdf');
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

.info-pdf {
  display: flex;
  align-items: center;
  background: #e8f5e9;
  color: #2e7d32;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  text-align: left;
  gap: 20px;
}

.info-pdf i {
  font-size: 2.5rem;
}

.info-pdf h4 {
  margin: 0 0 5px 0;
}

.info-pdf p {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.btn-pdf {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  border: none;
  padding: 14px 40px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 15px;
}

.btn-pdf:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(40, 167, 69, 0.3);
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

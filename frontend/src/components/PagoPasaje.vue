<template>
  <div class="payment-card">
    <h2>Pago de Pasajes <i class="fas fa-lock" style="font-size: 1.2rem; color: #28a745;"></i></h2>
    <p>Monto a pagar: <strong>${{ amount.toLocaleString() }} CLP</strong></p>

    <!-- Resumen del viaje -->
    <div class="resumen-viaje">
      <div class="resumen-item">
        <i class="fas fa-map-marker-alt"></i>
        <span>{{ detallesReserva.origen }} → {{ detallesReserva.destino }}</span>
      </div>
      <div class="resumen-item">
        <i class="fas fa-calendar-alt"></i>
        <span>{{ detallesReserva.fechaIda }}</span>
      </div>
      <div class="resumen-item">
        <i class="fas fa-users"></i>
        <span>{{ detallesReserva.pasajeros }} {{ detallesReserva.pasajeros === 1 ? 'pasajero' : 'pasajeros' }}</span>
      </div>
      <div class="resumen-item" v-if="detallesReserva.idaYVuelta">
        <i class="fas fa-exchange-alt"></i>
        <span>Ida y Vuelta ({{ detallesReserva.fechaVuelta }})</span>
      </div>
    </div>

    <!-- Mercado Pago Wallet Brick Container -->
    <div id="wallet_container"></div>

    <div v-if="loading" class="spinner">
      <i class="fas fa-circle-notch fa-spin"></i> Inicializando plataforma de pago seguro de Mercado Pago...
    </div>
    <div v-if="error" class="error-msg">
      <i class="fas fa-exclamation-triangle"></i> {{ error }}
    </div>

  </div>
</template>

<script>
import { loadMercadoPago } from '@mercadopago/sdk-js';

export default {
  name: 'PagoPasaje',
  props: ['detallesReserva'],
  data() {
    return {
      loading: true,
      error: null,
      brickController: null
    }
  },
  computed: {
    amount() {
      return this.detallesReserva?.precioTotal || 15000;
    },
    isLocalhost() {
      return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    }
  },
  async mounted() {
    try {
      // 1. Obtener Preference ID desde el backend Spring Boot
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/payments/create-payment-intent`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: this.amount })
      });

      if (!response.ok) {
        throw new Error('No se pudo conectar con el servidor de pago. Intenta nuevamente.');
      }

      const { preferenceId } = await response.json();

      // 2. Guardar estado para redirecciones de MercadoPago
      if (this.detallesReserva) {
        localStorage.setItem('reservaPendiente', JSON.stringify(this.detallesReserva));
      }

      // 3. Cargar SDK e inicializar Wallet Brick (Checkout Pro)
      await loadMercadoPago();
      const mpPublicKey = import.meta.env.VITE_MERCADOPAGO_PUBLIC_KEY || 'APP_USR-c3d6b0b2-ce90-4b34-b647-2c645b971818';
      const mp = new window.MercadoPago(mpPublicKey, {
         locale: 'es-CL'
      });

      const bricksBuilder = mp.bricks();

      // Failsafe: ocultar spinner si MercadoPago no responde en 8s
      setTimeout(() => { if (this.loading) this.loading = false; }, 8000);

      this.brickController = await bricksBuilder.create('wallet', 'wallet_container', {
        initialization: {
          preferenceId: preferenceId,
          redirectMode: 'self'
        },
        customization: { texts: { action: 'pay', valueProp: 'security_safety' } },
        callbacks: {
          onReady: () => {
             this.loading = false;
          },
          onError: (error) => {
             if (import.meta.env.DEV) console.error('Wallet Brick error:', error);
             this.error = "No se pudo cargar el módulo de pago. Por favor recarga la página.";
             this.loading = false;
          },
        },
      });

    } catch (err) {
      if (import.meta.env.DEV) console.error(err);
      this.error = 'Ocurrió un error al inicializar el pago. Por favor intenta nuevamente.';
      this.loading = false;
    }
  },
  unmounted() {
    if (this.brickController) {
      this.brickController.unmount();
    }
  }
}
</script>

<style scoped>
.payment-card {
  background: white;
  padding: 35px;
  width: 90%;
  max-width: 600px;
  margin: 40px auto;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  text-align: center;
}
.payment-card h2 {
  color: #0d286d;
  margin-bottom: 20px;
}
.resumen-viaje {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  margin-bottom: 25px;
}
.resumen-item {
  background: #f0f4ff;
  border: 1px solid #d0daf0;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #0d286d;
  display: flex;
  align-items: center;
  gap: 8px;
}
.resumen-item i {
  color: #174291;
}
#wallet_container {
  margin: 20px 0;
  min-height: 50px;
}
.spinner {
  margin: 15px 0;
  color: #0d286d;
  font-weight: bold;
}
.error-msg {
  color: #dc3545;
  margin-top: 10px;
  font-weight: bold;
}
</style>

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

    <!-- Modo Demostración (Simulación Rápida de Compra Completa) -->
    <div class="test-mode">
      <div class="test-divider"><span>Acceso Libre Universitario</span></div>
      <button @click="simularPago" class="btn-test" :disabled="testLoading">
        <i class="fas fa-graduation-cap"></i> 
        {{ testLoading ? 'Procesando simulación completa...' : 'Bypass Simulado (Sin Tarjeta)' }}
      </button>
      <p class="test-note">Este botón emite una factura instantánea gratuita para probar el portal a nivel universitario.</p>
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
      testLoading: false,
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
      // 1. Fetch Preference ID from Spring Boot Backend
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/payments/create-payment-intent`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: this.amount })
      });
      
      if (!response.ok) {
        throw new Error(await response.text() || 'Error conectando al backend de pago.');
      }
      
      const { preferenceId } = await response.json();
      console.log('Preference ID obtenido:', preferenceId);

      // 2. Prepare localStorage state for redirects so Vue app doesn't lose state
      if (this.detallesReserva) {
        localStorage.setItem('reservaPendiente', JSON.stringify(this.detallesReserva));
      }

      // 3. Load SDK and Initialize Wallet Brick (Checkout Pro)
      await loadMercadoPago();
      const mpPublicKey = import.meta.env.VITE_MERCADOPAGO_PUBLIC_KEY || 'APP_USR-c3d6b0b2-ce90-4b34-b647-2c645b971818';
      const mp = new window.MercadoPago(mpPublicKey, {
         locale: 'es-CL'
      });
      
      const bricksBuilder = mp.bricks();
      
      // Failsafe de tiempo para esconder el spinner si MP no responde
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
             console.log('Wallet Brick listo');
          },
          onError: (error) => {
             console.error('Wallet Brick error:', error);
             this.error = "No se pudo cargar el módulo de pago de Mercado Pago.";
             this.loading = false;
          },
        },
      });
      
    } catch (err) {
      console.error(err);
      this.error = err.message || "Error general del módulo de pago.";
      this.loading = false;
    }
  },
  methods: {
    async simularPago() {
      this.testLoading = true;
      this.error = null;
      let timeoutRender = setTimeout(() => {
         if (this.testLoading) {
             this.error = "Info: Tu servidor gratuito Render en EEUU parece estar Hibernando. ¡Demorará ~60 segundos en despertar el Backend Java por primera vez! Por favor no cierres la ventana...";
         }
      }, 7000);

      try {
        const apiUrl = import.meta.env.VITE_API_BASE_URL || "https://buses-venegas-backend.onrender.com";
        const response = await fetch(`${apiUrl}/api/payments/confirm`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            detalles: this.detallesReserva,
            paymentId: 'SIMULACRO-' + Date.now()
          })
        });
        
        clearTimeout(timeoutRender);
        if (response.ok) {
          const data = await response.json();
          this.detallesReserva.idReserva = data.idReserva;
          if (data.ticketHash) {
              this.detallesReserva.ticketHash = data.ticketHash;
          }
          // Emitimos @pago-exitoso para que App.vue inicie la redirección visual
          this.$emit('pago-exitoso', { status: 'approved', payment_id: 'SIMULACRO-' + Date.now() });
        } else {
          this.error = 'El servidor Vercel devolvió un problema (Código HTTP diferente a 200).';
          alert("Error crítico: El acceso al servidor online ha sido rechazado.\nAsegúrate que Vercel tiene su variable VITE_API_BASE_URL bien puesta.");
        }
      } catch (err) {
        clearTimeout(timeoutRender);
        console.error("EXCEPCION MORTAL JS EN FETCH:", err);
        this.error = 'Fallo crítico de Javascript de conexión en Router: ' + err.message;
        alert("¡Cables Roteados en Vercel! Network Connection Refused:\nProbablemente tienes seteado tu variable VITE_API_BASE_URL a http://localhost:8080 en vez del Render Production URL. \n\nLog: " + err.message);
      } finally {
        this.testLoading = false;
      }
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
.test-mode {
  margin-top: 30px;
}
.test-divider {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.test-divider::before,
.test-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #dee2e6;
}
.test-divider span {
  padding: 0 15px;
  color: #6c757d;
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
}
.btn-test {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  border: none;
  padding: 14px 35px;
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
}
.btn-test:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(40, 167, 69, 0.3);
}
.btn-test:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.test-note {
  color: #6c757d;
  font-size: 0.8rem;
  margin-top: 10px;
}
</style>

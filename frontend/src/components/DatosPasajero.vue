<template>
  <div class="datos-container">
    <h2>Datos de los Pasajeros</h2>
    <p class="instrucciones">Tu viaje de <strong>{{ detallesReserva.origen }}</strong> a <strong>{{ detallesReserva.destino }}</strong> está casi listo. Ingresa los datos exactos.</p>

    <form @submit.prevent="continuar">
      <div v-for="(asiento, index) in detallesReserva.asientos" :key="asiento" class="pasajero-card">
        <div class="card-header">
          <h3>Pasajero {{ index + 1 }}</h3>
          <span class="badge-asiento">Asiento {{ asiento }}</span>
        </div>
        
        <div class="form-grid">
          <div class="form-group">
            <label>Nombres</label>
            <input type="text" v-model="pasajeros[index].nombre" :class="{'input-error': pasajeros[index].errorNombre}" required placeholder="Ej. Juan Andrés">
            <span v-if="pasajeros[index].errorNombre" class="msj-error">Nombre insuficiente.</span>
          </div>
          <div class="form-group">
            <label>Apellidos</label>
            <input type="text" v-model="pasajeros[index].apellidos" :class="{'input-error': pasajeros[index].errorApellido}" required placeholder="Ej. Pérez González">
            <span v-if="pasajeros[index].errorApellido" class="msj-error">Apellido insuficiente.</span>
          </div>
          <div class="form-group">
            <label>RUT / Pasaporte</label>
            <input type="text" v-model="pasajeros[index].rut" :class="{'input-error': pasajeros[index].errorRut}" required placeholder="Ej. 12.345.678-9">
            <span v-if="pasajeros[index].errorRut" class="msj-error">RUT inválido. Revisa el formato.</span>
          </div>
          <div class="form-group">
            <label>Correo Electrónico (Para envío de boleta)</label>
            <input type="email" v-model="pasajeros[index].email" :class="{'input-error': pasajeros[index].errorEmail}" required placeholder="usuario@correo.com">
            <span v-if="pasajeros[index].errorEmail" class="msj-error">Correo inválido o mal escrito.</span>
          </div>
        </div>
      </div>

      <div v-if="errorGeneral" class="alerta-general">
        <i class="fas fa-exclamation-triangle"></i> {{ errorGeneral }}
      </div>

      <div class="actions">
        <button type="button" @click="$emit('volver')" class="btn-volver"><i class="fas fa-arrow-left"></i> Volver</button>
        <button type="submit" class="btn-continuar">Ir al Pago <i class="fas fa-lock"></i></button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  name: 'DatosPasajero',
  props: {
    detallesReserva: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      pasajeros: [],
      errorGeneral: null
    }
  },
  created() {
    if (this.detallesReserva && this.detallesReserva.asientos) {
      this.detallesReserva.asientos.forEach(asiento => {
        this.pasajeros.push({
          asiento: asiento,
          nombre: '',
          apellidos: '',
          rut: '',
          email: '',
          errorNombre: false,
          errorApellido: false,
          errorRut: false,
          errorEmail: false
        });
      });
    }
  },
  methods: {
    validaRut(rutCompleto) {
      // Limpiar: quitar puntos, guiones y espacios
      let limpio = rutCompleto.replace(/[.\-\s]/g, '').toLowerCase();
      if (limpio.length < 7 || limpio.length > 9) return false;

      let digv = limpio.slice(-1);
      let cuerpo = limpio.slice(0, -1);

      // Verificar que el cuerpo sean solo dígitos
      if (!/^\d+$/.test(cuerpo)) return false;

      // Calcular dígito verificador iterando el string
      let suma = 0;
      let multiplicador = 2;
      for (let i = cuerpo.length - 1; i >= 0; i--) {
        suma += parseInt(cuerpo[i]) * multiplicador;
        multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
      }

      let resto = 11 - (suma % 11);
      let dvEsperado = resto === 11 ? '0' : resto === 10 ? 'k' : resto.toString();
      return dvEsperado === digv;
    },
    validaEmail(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(email);
    },
    continuar() {
      let hayErrores = false;
      this.pasajeros.forEach((p) => {
        p.errorRut = false;
        p.errorEmail = false;
        p.errorNombre = false;
        p.errorApellido = false;
        
        if (!p.nombre || p.nombre.trim().length < 2) {
          p.errorNombre = true;
          hayErrores = true;
        }
        if (!p.apellidos || p.apellidos.trim().length < 2) {
          p.errorApellido = true;
          hayErrores = true;
        }
        if (!this.validaRut(p.rut)) {
          p.errorRut = true;
          hayErrores = true;
        }
        if (!this.validaEmail(p.email)) {
          p.errorEmail = true;
          hayErrores = true;
        }
      });
      
      if (hayErrores) {
        this.errorGeneral = "Por favor corrige en color rojo los datos con formato inválido antes de proceder a pagar.";
        return;
      }
      
      this.errorGeneral = null;
      this.$emit('datos-ingresados', this.pasajeros);
    }
  }
}
</script>

<style scoped>
.datos-container {
  background: white;
  padding: 40px;
  width: 90%;
  max-width: 900px;
  margin: -30px auto 40px auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

h2 {
  color: #0d286d;
  text-align: center;
  margin-bottom: 10px;
}

.instrucciones {
  color: #666;
  text-align: center;
  margin-bottom: 30px;
}

.pasajero-card {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 10px;
}

.card-header h3 {
  color: #0d286d;
  margin: 0;
}

.badge-asiento {
  background: #ffc107;
  color: #0d286d;
  padding: 5px 15px;
  border-radius: 20px;
  font-weight: bold;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

input {
  padding: 12px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: #0d286d;
  box-shadow: 0 0 0 2px rgba(13, 40, 109, 0.2);
}

.msj-error {
  color: #dc3545;
  font-size: 0.8rem;
  margin-top: 5px;
  font-weight: 500;
}

.input-error {
  border-color: #dc3545;
  background-color: #fff8f8;
}

.input-error:focus {
  box-shadow: 0 0 0 2px rgba(220, 53, 69, 0.2);
}

.alerta-general {
  background: #fff3cd;
  color: #856404;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  font-weight: 600;
  text-align: center;
  border-left: 4px solid #ffeeba;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

.btn-volver {
  background: #e9ecef;
  color: #495057;
  border: none;
  padding: 15px 30px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-volver:hover {
  background: #ced4da;
}

.btn-continuar {
  background: #0d286d;
  color: white;
  border: none;
  padding: 15px 40px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-continuar:hover {
  background: #174291;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>

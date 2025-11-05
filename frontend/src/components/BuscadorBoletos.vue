<template>
  <div class="form-card">
    <form @submit.prevent="buscarPasajes">
      <div class="form-row">
        <div class="form-group">
          <label for="origen">
            <i class="fas fa-map-marker-alt"></i> Origen
          </label>
          <input
              type="text"
              id="origen"
              placeholder="¿Desde dónde viajas?"
              v-model="form.origen"
              required
          />
        </div>

        <div class="form-group">
          <label for="destino">
            <i class="fas fa-map-marker-alt"></i> Destino
          </label>
          <input
              type="text"
              id="destino"
              placeholder="¿A dónde vas?"
              v-model="form.destino"
              required
          />
        </div>

        <div class="form-group">
          <label for="fechaIda">
            <i class="fas fa-calendar-alt"></i> Fecha de ida
          </label>
          <input
              type="date"
              id="fechaIda"
              v-model="form.fechaIda"
              :min="fechaMinima"
              required
          />
        </div>

        <div class="form-group">
          <label for="fechaVuelta">
            <i class="fas fa-calendar-alt"></i> Fecha de vuelta
            <span class="opcional">(Opcional)</span>
          </label>
          <input
              type="date"
              id="fechaVuelta"
              v-model="form.fechaVuelta"
              :min="form.fechaIda || fechaMinima"
          />
        </div>

        <div class="form-group">
          <label for="pasajeros">
            <i class="fas fa-users"></i> Pasajeros
          </label>
          <select id="pasajeros" v-model="form.pasajeros">
            <option v-for="n in 10" :key="n" :value="n">{{ n }} {{ n === 1 ? 'pasajero' : 'pasajeros' }}</option>
          </select>
        </div>
      </div>

      <button type="submit" class="btn-buscar">
        <i class="fas fa-search"></i> BUSCAR PASAJES
      </button>
    </form>

    <div class="opciones-adicionales">
      <label class="checkbox-container">
        <input type="checkbox" v-model="form.idaYVuelta">
        <span>Viaje ida y vuelta</span>
      </label>
      <label class="checkbox-container">
        <input type="checkbox" v-model="form.flexible">
        <span>Fechas flexibles</span>
      </label>
    </div>
  </div>
</template>

<script>
export default {
  name: "BuscadorBoletos",
  data() {
    return {
      form: {
        origen: "",
        destino: "",
        fechaIda: "",
        fechaVuelta: "",
        pasajeros: 1,
        idaYVuelta: false,
        flexible: false,
      },
      fechaMinima: new Date().toISOString().split('T')[0]
    };
  },
  methods: {
    buscarPasajes() {
      console.log("Buscando pasajes:", this.form);
      // Aquí se conectará con el backend
      alert(`Buscando pasajes de ${this.form.origen} a ${this.form.destino}`);
    },
  },
};
</script>

<style scoped>
.form-card {
  background: linear-gradient(135deg, #0d286d 0%, #174291 100%);
  padding: 35px;
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  top: -30px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
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
  color: #ffeb3b;
}

.opcional {
  font-size: 0.75rem;
  font-weight: 400;
  opacity: 0.7;
  margin-left: 5px;
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

input::placeholder {
  color: #999;
}

.btn-buscar {
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
}

.btn-buscar:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 235, 59, 0.4);
}

.btn-buscar:active {
  transform: translateY(0);
}

.opciones-adicionales {
  display: flex;
  gap: 30px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.checkbox-container {
  display: flex;
  align-items: center;
  color: white;
  cursor: pointer;
  user-select: none;
}

.checkbox-container input[type="checkbox"] {
  width: auto;
  margin-right: 8px;
  cursor: pointer;
  width: 18px;
  height: 18px;
}

.checkbox-container span {
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .form-card {
    padding: 25px 20px;
    top: -20px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .btn-buscar {
    padding: 14px 30px;
    font-size: 1rem;
  }

  .opciones-adicionales {
    flex-direction: column;
    gap: 15px;
  }
}
</style>
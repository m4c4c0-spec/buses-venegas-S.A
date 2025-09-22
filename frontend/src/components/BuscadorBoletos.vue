<template>
  <div class="form-card">
    <form @submit.prevent="buscarPasajes">
      <div class="form-group">
        <label for="origen">Origen</label>
        <input type="text" id="origen" placeholder="Ingresa tu origen" v-model="form.origen" />
      </div>
      <div class="form-group">
        <label for="destino">Destino</label>
        <input type="text" id="destino" placeholder="Ingresa tu destino" v-model="form.destino" />
      </div>
      <div class="form-group">
        <label for="fechaIda">Fecha de ida</label>
        <input type="date" id="fechaIda" v-model="form.fechaIda" />
      </div>
      <div class="form-group">
        <label for="fechaVuelta">Fecha de vuelta</label>
        <input type="date" id="fechaVuelta" v-model="form.fechaVuelta" />
      </div>
      <button type="submit" class="btn-buscar">BUSCAR</button>
    </form>
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
      },
    };
  },
  methods: {
    buscarPasajes() {
      window.open("about:blank", "_blank");
    },
  },
};
</script>

<style scoped>
.form-card {
  background-color: #0d286d;
  padding: 20px;
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  position: relative;
  top: -30px;
}

.form-card form {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 15px;
}

.form-group {
  flex-grow: 1;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 8px;
  color: white;
}

input {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 4px;
  box-sizing: border-box;
}

.btn-buscar {
  background-color: #0d286d;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 4px;
  cursor: pointer;
}
</style>


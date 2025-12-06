<template>
  <div class="passenger-form-group">
    <div class="form-header">
      <i class="fas fa-user"></i>
      <h4>Pasajero - Asiento {{ seatNumber }}</h4>
    </div>
    
    <div class="form-body">
      <div class="form-row">
        <div class="form-item">
          <label>Nombre</label>
          <input 
            type="text" 
            placeholder="Nombres" 
            :value="modelValue.firstName"
            @input="updateField('firstName', ($event.target as HTMLInputElement).value)"
          />
        </div>
        <div class="form-item">
          <label>Apellido</label>
          <input 
            type="text" 
            placeholder="Apellidos" 
            :value="modelValue.lastName"
            @input="updateField('lastName', ($event.target as HTMLInputElement).value)"
          />
        </div>
      </div>

      <div class="form-row">
         <div class="form-item small">
          <label>Doc.</label>
          <select 
            :value="modelValue.documentType"
            @change="updateField('documentType', ($event.target as HTMLSelectElement).value)"
          >
            <option value="RUT">RUT</option>
            <option value="DNI">DNI</option>
            <option value="PAS">PAS</option>
          </select>
        </div>
        <div class="form-item">
          <label>Número Documento</label>
          <input 
            type="text" 
            placeholder="Ej: 12.345.678-9" 
            :value="modelValue.documentNumber"
            @input="updateField('documentNumber', ($event.target as HTMLInputElement).value)"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-item">
          <label>Email</label>
          <input 
            type="email" 
            placeholder="contacto@ejemplo.com" 
            :value="modelValue.email"
            @input="updateField('email', ($event.target as HTMLInputElement).value)"
          />
        </div>
        <div class="form-item">
          <label>Teléfono</label>
          <input 
            type="tel" 
            placeholder="+56 9 ..." 
            :value="modelValue.phone"
            @input="updateField('phone', ($event.target as HTMLInputElement).value)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// defineProps and defineEmits are available in script setup

interface PassengerData {
  firstName: string;
  lastName: string;
  documentType: string;
  documentNumber: string;
  email: string;
  phone: string;
}

const props = defineProps<{
  seatNumber: string;
  modelValue: PassengerData;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: PassengerData): void;
}>();

const updateField = (field: keyof PassengerData, value: string) => {
  emit('update:modelValue', {
    ...props.modelValue,
    [field]: value
  });
};
</script>

<style scoped>
.passenger-form-group {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  margin-bottom: 25px;
  overflow: hidden;
}

.form-header {
  background: rgba(0, 0, 0, 0.2);
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.form-header h4 {
  margin: 0;
  color: #ffeb3b;
  font-size: 1rem;
}

.form-header i {
  color: white;
}

.form-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item.small {
    flex: 0 0 80px;
}

label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 600;
}

input, select {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1); /* Glassmorphism style */
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 0.95rem;
  transition: all 0.3s;
  box-sizing: border-box;
}

input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

input:focus, select:focus {
  outline: none;
  border-color: #ffeb3b;
  background: rgba(255, 255, 255, 0.15);
}

select option {
  background: #0d286d; /* Dark background for options */
  color: white;
}

@media (max-width: 600px) {
  .form-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .form-item.small {
      flex: auto;
  }
}
</style>

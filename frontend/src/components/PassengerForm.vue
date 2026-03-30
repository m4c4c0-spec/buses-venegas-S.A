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
            @blur="touchField('firstName')"
            :class="{ 'invalid': errors.firstName && touched.firstName }"
          />
          <span v-if="errors.firstName && touched.firstName" class="error-text">Requerido</span>
        </div>
        <div class="form-item">
          <label>Apellido</label>
          <input 
            type="text" 
            placeholder="Apellidos" 
            :value="modelValue.lastName"
            @input="updateField('lastName', ($event.target as HTMLInputElement).value)"
            @blur="touchField('lastName')"
             :class="{ 'invalid': errors.lastName && touched.lastName }"
          />
           <span v-if="errors.lastName && touched.lastName" class="error-text">Requerido</span>
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
            @blur="touchField('documentNumber')"
            :class="{ 'invalid': errors.documentNumber && touched.documentNumber }"
          />
          <span v-if="errors.documentNumber && touched.documentNumber" class="error-text">Formato inválido</span>
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
            @blur="touchField('email')"
            :class="{ 'invalid': errors.email && touched.email }"
          />
          <span v-if="errors.email && touched.email" class="error-text">Email inválido</span>
        </div>
        <div class="form-item">
          <label>Teléfono</label>
          <input 
            type="tel" 
            placeholder="+56 9 ..." 
            :value="modelValue.phone"
            @input="updateField('phone', ($event.target as HTMLInputElement).value)"
             @blur="touchField('phone')"
             :class="{ 'invalid': errors.phone && touched.phone }"
          />
           <span v-if="errors.phone && touched.phone" class="error-text">Min 9 dígitos</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, reactive } from 'vue';

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

const touched = reactive({
    firstName: false,
    lastName: false,
    documentNumber: false,
    email: false,
    phone: false
});

const errors = computed(() => {
    return {
        firstName: props.modelValue.firstName.length < 2,
        lastName: props.modelValue.lastName.length < 2,
        documentNumber: props.modelValue.documentNumber.length < 7, // Basic length check
        email: !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(props.modelValue.email),
        phone: props.modelValue.phone.length < 9
    };
});

const updateField = (field: keyof PassengerData, value: string) => {
  emit('update:modelValue', {
    ...props.modelValue,
    [field]: value
  });
};

const touchField = (field: keyof typeof touched) => {
    // @ts-ignore
    touched[field] = true;
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

input.invalid {
    border-color: #f44336;
    background: rgba(244, 67, 54, 0.1);
}

.error-text {
    color: #f44336;
    font-size: 0.75rem;
    margin-top: 2px;
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

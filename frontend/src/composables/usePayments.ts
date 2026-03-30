import { ref } from 'vue';
import { paymentService } from '../services/paymentService';
import type { PaymentResponse, PaymentMethod } from '../types/payment';

/**
 * Composable para gestionar pagos
 */
export function usePayments() {
    const currentPayment = ref<PaymentResponse | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const success = ref<string | null>(null);

    /**
     * Limpiar mensajes
     */
    const clearMessages = () => {
        error.value = null;
        success.value = null;
    };

    /**
     * Procesar un pago
     */
    const processPayment = async (bookingId: number, method: PaymentMethod): Promise<PaymentResponse | null> => {
        loading.value = true;
        clearMessages();

        try {
            const payment = await paymentService.processPayment({ bookingId, method });
            currentPayment.value = payment;

            if (payment.status === 'APROBADO') {
                success.value = `¡Pago procesado exitosamente! Transacción: ${payment.transactionId}`;
            } else {
                error.value = 'El pago no pudo ser procesado. Intenta nuevamente.';
            }

            return payment;
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || 'Error al procesar el pago';
            error.value = errorMessage;
            console.error('Error procesando pago:', err);
            return null;
        } finally {
            loading.value = false;
        }
    };

    /**
     * Obtener pago por booking
     */
    const getPaymentByBooking = async (bookingId: number): Promise<PaymentResponse | null> => {
        loading.value = true;
        clearMessages();

        try {
            const payment = await paymentService.getPaymentByBooking(bookingId);
            currentPayment.value = payment;
            return payment;
        } catch (err: any) {
            // Si es 404, significa que no hay pago
            if (err.response?.status !== 404) {
                error.value = 'Error al obtener información del pago';
            }
            return null;
        } finally {
            loading.value = false;
        }
    };

    return {
        currentPayment,
        loading,
        error,
        success,
        processPayment,
        getPaymentByBooking,
        clearMessages,
    };
}

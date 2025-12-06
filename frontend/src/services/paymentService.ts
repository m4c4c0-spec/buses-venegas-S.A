import api from './api';
import type { PaymentResponse, ProcessPaymentRequest } from '../types/payment';

/**
 * Servicio para operaciones de pagos
 */
export const paymentService = {
    /**
     * Procesar un pago para una reserva
     */
    async processPayment(request: ProcessPaymentRequest): Promise<PaymentResponse> {
        const response = await api.post<PaymentResponse>('/payments', request);
        return response.data;
    },

    /**
     * Obtener un pago por ID
     */
    async getPaymentById(paymentId: number): Promise<PaymentResponse> {
        const response = await api.get<PaymentResponse>(`/payments/${paymentId}`);
        return response.data;
    },

    /**
     * Obtener pago por ID de reserva
     */
    async getPaymentByBooking(bookingId: number): Promise<PaymentResponse> {
        const response = await api.get<PaymentResponse>(`/payments/booking/${bookingId}`);
        return response.data;
    },
};

export default paymentService;

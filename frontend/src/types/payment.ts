/**
 * Método de pago disponible
 */
export type PaymentMethod =
    | 'TARJETA_CREDITO'
    | 'TARJETA_DEBITO'
    | 'WEBPAY'
    | 'MERCADOPAGO'
    | 'BANK_TRANSFER';

/**
 * Estado del pago
 */
export type PaymentStatus = 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | 'REEMBOLSADO';

/**
 * Solicitud para procesar un pago
 */
export interface ProcessPaymentRequest {
    bookingId: number;
    method: PaymentMethod;
}

/**
 * Respuesta del backend para un pago
 */
export interface PaymentResponse {
    id: number;
    bookingId: number;
    amount: number;
    method: PaymentMethod;
    status: PaymentStatus;
    transactionId: string;
    createdAt: string;
}

/**
 * Información de tarjeta para UI (no se envía al backend)
 */
export interface CardInfo {
    cardNumber: string;
    cardHolder: string;
    expiryMonth: string;
    expiryYear: string;
    cvv: string;
}

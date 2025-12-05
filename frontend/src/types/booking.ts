/**
 * Estado de una reserva
 */
export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';

/**
 * Información del pasajero
 */
export interface PassengerRequest {
    firstName: string;
    lastName: string;
    documentNumber: string;
    documentType: string;
    email: string;
    phone: string;
}

/**
 * Solicitud para crear una reserva
 */
export interface CreateBookingRequest {
    tripId: string;
    userId: string;
    seatNumber: string;
    passenger: PassengerRequest;
}

/**
 * Respuesta del backend para una reserva
 */
export interface BookingResponse {
    id: number;
    tripId: number;
    userId: number;
    seats: string[];
    status: BookingStatus;
    totalAmount: number;
    createdAt: string; // ISO 8601 format
    expiresAt: string; // ISO 8601 format
}

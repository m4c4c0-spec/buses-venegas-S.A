import api from './api';
import type { BookingResponse, CreateBookingRequest } from '../types/booking';

/**
 * Servicio para gestionar operaciones relacionadas con reservas
 */
export const bookingService = {
    /**
     * Crear una nueva reserva
     * @param data - Datos de la reserva
     * @returns Información de la reserva creada
     */
    async createBooking(data: CreateBookingRequest): Promise<BookingResponse> {
        try {
            const response = await api.post<BookingResponse>('/bookings', data);
            return response.data;
        } catch (error) {
            console.error('Error al crear reserva:', error);
            throw error;
        }
    },

    /**
     * Obtener todas las reservas de un usuario
     * @param userId - ID del usuario
     * @returns Lista de reservas del usuario
     */
    async getUserBookings(userId: string): Promise<BookingResponse[]> {
        try {
            const response = await api.get<BookingResponse[]>(`/bookings/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error(`Error al obtener reservas del usuario ${userId}:`, error);
            throw error;
        }
    },

    /**
     * Confirmar una reserva
     * @param bookingId - ID de la reserva
     * @returns Información de la reserva confirmada
     */
    async confirmBooking(bookingId: string): Promise<BookingResponse> {
        try {
            const response = await api.put<BookingResponse>(`/bookings/${bookingId}/confirm`);
            return response.data;
        } catch (error) {
            console.error(`Error al confirmar reserva ${bookingId}:`, error);
            throw error;
        }
    },

    /**
     * Cancelar una reserva
     * @param bookingId - ID de la reserva
     */
    async cancelBooking(bookingId: string): Promise<void> {
        try {
            await api.delete(`/bookings/${bookingId}`);
        } catch (error) {
            console.error(`Error al cancelar reserva ${bookingId}:`, error);
            throw error;
        }
    },
};

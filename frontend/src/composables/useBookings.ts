import { ref, type Ref } from 'vue';
import { bookingService } from '../services/bookingService';
import { getUserFriendlyErrorMessage } from '../utils/errorHandler';
import type { BookingResponse, CreateBookingRequest } from '../types/booking';

/**
 * Composable para gestionar operaciones de reservas
 * Proporciona estado reactivo y métodos para crear, confirmar y cancelar reservas
 */
export function useBookings() {
    // Estado reactivo
    const bookings: Ref<BookingResponse[]> = ref([]);
    const currentBooking: Ref<BookingResponse | null> = ref(null);
    const loading: Ref<boolean> = ref(false);
    const error: Ref<string | null> = ref(null);
    const success: Ref<string | null> = ref(null);

    /**
     * Crear una nueva reserva
     * @param data - Datos de la reserva
     */
    const createBooking = async (data: CreateBookingRequest): Promise<BookingResponse | null> => {
        loading.value = true;
        error.value = null;
        success.value = null;

        try {
            const result = await bookingService.createBooking(data);
            currentBooking.value = result;
            success.value = 'Reserva creada exitosamente';
            return result;
        } catch (err) {
            error.value = getUserFriendlyErrorMessage(err);
            console.error('Error en createBooking:', err);
            return null;
        } finally {
            loading.value = false;
        }
    };

    /**
     * Obtener reservas de un usuario
     * @param userId - ID del usuario
     */
    const getUserBookings = async (userId: string): Promise<void> => {
        loading.value = true;
        error.value = null;
        bookings.value = [];

        try {
            const result = await bookingService.getUserBookings(userId);
            bookings.value = result;

            if (result.length === 0) {
                error.value = 'No se encontraron reservas para este usuario.';
            }
        } catch (err) {
            error.value = getUserFriendlyErrorMessage(err);
            console.error('Error en getUserBookings:', err);
        } finally {
            loading.value = false;
        }
    };

    /**
     * Confirmar una reserva
     * @param bookingId - ID de la reserva
     */
    const confirmBooking = async (bookingId: string): Promise<boolean> => {
        loading.value = true;
        error.value = null;
        success.value = null;

        try {
            const result = await bookingService.confirmBooking(bookingId);
            currentBooking.value = result;
            success.value = 'Reserva confirmada exitosamente';
            return true;
        } catch (err) {
            error.value = getUserFriendlyErrorMessage(err);
            console.error('Error en confirmBooking:', err);
            return false;
        } finally {
            loading.value = false;
        }
    };

    /**
     * Cancelar una reserva
     * @param bookingId - ID de la reserva
     */
    const cancelBooking = async (bookingId: string): Promise<boolean> => {
        loading.value = true;
        error.value = null;
        success.value = null;

        try {
            await bookingService.cancelBooking(bookingId);
            success.value = 'Reserva cancelada exitosamente';

            // Remover de la lista si existe
            bookings.value = bookings.value.filter(b => b.id.toString() !== bookingId);

            return true;
        } catch (err) {
            error.value = getUserFriendlyErrorMessage(err);
            console.error('Error en cancelBooking:', err);
            return false;
        } finally {
            loading.value = false;
        }
    };

    /**
     * Limpiar estado
     */
    const clearState = (): void => {
        bookings.value = [];
        currentBooking.value = null;
        error.value = null;
        success.value = null;
    };

    return {
        // Estado
        bookings,
        currentBooking,
        loading,
        error,
        success,

        // Métodos
        createBooking,
        getUserBookings,
        confirmBooking,
        cancelBooking,
        clearState,
    };
}

import { ref, type Ref } from 'vue';
import { tripService } from '../services/tripService';
import { getUserFriendlyErrorMessage } from '../utils/errorHandler';
import type { TripResponse } from '../types/trip';

/**
 * Composable para gestionar operaciones de viajes
 * Proporciona estado reactivo y métodos para buscar viajes
 */
export function useTrips() {
    // Estado reactivo
    const trips: Ref<TripResponse[]> = ref([]);
    const loading: Ref<boolean> = ref(false);
    const error: Ref<string | null> = ref(null);

    /**
     * Buscar viajes disponibles
     * @param origin - Ciudad de origen
     * @param destination - Ciudad de destino
     * @param date - Fecha del viaje (yyyy-MM-dd)
     */
    const searchTrips = async (
        origin: string,
        destination: string,
        date: string
    ): Promise<void> => {
        loading.value = true;
        error.value = null;
        trips.value = [];

        try {
            const result = await tripService.searchTrips(origin, destination, date);
            trips.value = result;

            if (result.length === 0) {
                error.value = 'No se encontraron viajes disponibles para la fecha seleccionada.';
            }
        } catch (err) {
            error.value = getUserFriendlyErrorMessage(err);
            console.error('Error en searchTrips:', err);
        } finally {
            loading.value = false;
        }
    };

    /**
     * Limpiar resultados de búsqueda
     */
    const clearTrips = (): void => {
        trips.value = [];
        error.value = null;
    };

    return {
        // Estado
        trips,
        loading,
        error,

        // Métodos
        searchTrips,
        clearTrips,
    };
}

import api from './api';
import type { TripResponse } from '../types/trip';

/**
 * Servicio para gestionar operaciones relacionadas con viajes
 */
export const tripService = {
    /**
     * Buscar viajes disponibles
     * @param origin - Ciudad de origen
     * @param destination - Ciudad de destino
     * @param date - Fecha del viaje (formato: yyyy-MM-dd)
     * @returns Lista de viajes disponibles
     */
    async searchTrips(
        origin: string,
        destination: string,
        date: string
    ): Promise<TripResponse[]> {
        try {
            const response = await api.get<TripResponse[]>('/trips/search', {
                params: {
                    origin,
                    destination,
                    date,
                },
            });
            return response.data;
        } catch (error) {
            console.error('Error al buscar viajes:', error);
            throw error;
        }
    },

    /**
     * Obtener un viaje por su ID
     * @param tripId - ID del viaje
     * @returns Información del viaje
     */
    async getTripById(tripId: string): Promise<TripResponse> {
        try {
            const response = await api.get<TripResponse>(`/trips/${tripId}`);
            return response.data;
        } catch (error) {
            console.error(`Error al obtener viaje ${tripId}:`, error);
            throw error;
        }
    },
};

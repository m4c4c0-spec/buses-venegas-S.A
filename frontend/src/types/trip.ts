/**
 * Respuesta del backend para un viaje
 */
export interface TripResponse {
    id: number;
    origin: string;
    destination: string;
    departureTime: string; // ISO 8601 format: "yyyy-MM-dd'T'HH:mm:ss"
    arrivalTime: string;   // ISO 8601 format: "yyyy-MM-dd'T'HH:mm:ss"
    basePrice: number;
    availableSeats: number;
    busInfo?: string;
}

/**
 * Parámetros para buscar viajes
 */
export interface SearchTripsParams {
    origin: string;
    destination: string;
    date: string; // formato: yyyy-MM-dd
}

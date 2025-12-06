import { AxiosError } from 'axios';
import type { ApiError } from '../types/api';

/**
 * Procesa errores de Axios y los convierte en objetos ApiError
 * @param error - Error capturado
 * @returns Objeto ApiError con información del error
 */
export function handleApiError(error: unknown): ApiError {
    if (error instanceof AxiosError) {
        if (error.response) {
            // El servidor respondió con un código de error
            return {
                message: error.response.data?.message || getDefaultErrorMessage(error.response.status),
                status: error.response.status,
                code: error.response.data?.code,
                details: error.response.data,
            };
        } else if (error.request) {
            // La solicitud se hizo pero no hubo respuesta
            return {
                message: 'No se pudo conectar con el servidor. Verifica tu conexión a internet.',
                status: 0,
            };
        }
    }

    // Error desconocido
    return {
        message: 'Ocurrió un error inesperado. Por favor, intenta nuevamente.',
    };
}

/**
 * Obtiene un mensaje de error por defecto según el código de estado HTTP
 * @param status - Código de estado HTTP
 * @returns Mensaje de error
 */
function getDefaultErrorMessage(status: number): string {
    switch (status) {
        case 400:
            return 'La solicitud contiene datos inválidos.';
        case 401:
            return 'No estás autorizado. Por favor, inicia sesión.';
        case 403:
            return 'No tienes permisos para realizar esta acción.';
        case 404:
            return 'El recurso solicitado no fue encontrado.';
        case 409:
            return 'Conflicto con el estado actual del recurso.';
        case 500:
            return 'Error interno del servidor. Por favor, intenta más tarde.';
        case 503:
            return 'El servicio no está disponible temporalmente.';
        default:
            return `Error del servidor (código ${status}).`;
    }
}

/**
 * Obtiene un mensaje de error amigable para el usuario
 * @param error - Error capturado
 * @returns Mensaje de error amigable
 */
export function getUserFriendlyErrorMessage(error: unknown): string {
    const apiError = handleApiError(error);
    return apiError.message;
}

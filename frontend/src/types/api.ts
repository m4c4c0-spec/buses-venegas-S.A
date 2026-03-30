/**
 * Estructura de error de la API
 */
export interface ApiError {
    message: string;
    status?: number;
    code?: string;
    details?: unknown;
}

/**
 * Respuesta genérica de la API
 */
export interface ApiResponse<T> {
    data: T;
    message?: string;
    success: boolean;
}

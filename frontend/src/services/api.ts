import axios, { type AxiosInstance, AxiosError } from 'axios';
import { getAccessToken, refreshAccessToken, logout } from './authService';

// Configuración base de Axios
const api: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor de Request: inyecta el token JWT en cada petición
api.interceptors.request.use(
    (config) => {
        const token = getAccessToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        // Solo loguear en desarrollo
        if (import.meta.env.DEV) {
            console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`);
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Flag para evitar bucles infinitos de refresh
let isRefreshing = false;
let failedQueue: Array<{ resolve: (token: string) => void; reject: (error: unknown) => void }> = [];

function processQueue(error: unknown, token: string | null = null) {
    failedQueue.forEach(({ resolve, reject }) => {
        if (error) {
            reject(error);
        } else {
            resolve(token!);
        }
    });
    failedQueue = [];
}

// Interceptor de Response: manejo de errores y auto-refresh del token
api.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as typeof error.config & { _retry?: boolean };

        // Si el access token expiró (401) y no es un retry, intentar refrescarlo
        if (error.response?.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                // Si ya hay un refresh en curso, encolar la petición
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                }).then((token) => {
                    originalRequest!.headers!['Authorization'] = `Bearer ${token}`;
                    return api(originalRequest!);
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                const newToken = await refreshAccessToken();
                processQueue(null, newToken);
                originalRequest!.headers!['Authorization'] = `Bearer ${newToken}`;
                return api(originalRequest!);
            } catch (refreshError) {
                processQueue(refreshError, null);
                // El refresh falló: el usuario debe volver a iniciar sesión
                await logout();
                if (import.meta.env.DEV) {
                    console.warn('Sesión expirada. Redirigiendo al login...');
                }
                // Redirigir al inicio para que el usuario inicie sesión
                window.dispatchEvent(new CustomEvent('auth:session-expired'));
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        // Manejo de errores no relacionados con autenticación
        if (import.meta.env.DEV) {
            const status = error.response?.status;
            const url = error.config?.url;
            console.error(`[API Error] ${status} ${url}`);
        }

        return Promise.reject(error);
    }
);

export default api;

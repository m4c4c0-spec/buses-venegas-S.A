import axios, { type AxiosInstance, AxiosError } from 'axios';

// Configuración base de Axios
const api: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor de Request
// Aquí se pueden agregar tokens de autenticación, headers personalizados, etc.
api.interceptors.request.use(
    (config) => {
        // Ejemplo: Agregar token de autenticación
        // const token = localStorage.getItem('authToken');
        // if (token) {
        //   config.headers.Authorization = `Bearer ${token}`;
        // }

        console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`);
        return config;
    },
    (error) => {
        console.error('[API Request Error]', error);
        return Promise.reject(error);
    }
);

// Interceptor de Response
// Manejo centralizado de errores
api.interceptors.response.use(
    (response) => {
        console.log(`[API Response] ${response.status} ${response.config.url}`);
        return response;
    },
    (error: AxiosError) => {
        if (error.response) {
            // El servidor respondió con un código de error (4xx, 5xx)
            console.error(
                `[API Error] ${error.response.status} ${error.config?.url}`,
                error.response.data
            );

            // Manejo específico por código de error
            switch (error.response.status) {
                case 400:
                    console.error('Solicitud incorrecta:', error.response.data);
                    break;
                case 401:
                    console.error('No autorizado - redirigir a login');
                    // Aquí se podría redirigir al login
                    break;
                case 404:
                    console.error('Recurso no encontrado');
                    break;
                case 500:
                    console.error('Error del servidor');
                    break;
            }
        } else if (error.request) {
            // La solicitud se hizo pero no hubo respuesta
            console.error('[API Error] Sin respuesta del servidor', error.request);
        } else {
            // Error en la configuración de la solicitud
            console.error('[API Error] Error de configuración:', error.message);
        }

        return Promise.reject(error);
    }
);

export default api;

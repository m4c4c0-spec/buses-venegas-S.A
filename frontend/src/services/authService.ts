/**
 * authService.ts
 * Servicio de autenticación JWT para el frontend.
 *
 * Maneja:
 *  - Login (obtiene access + refresh token)
 *  - Logout (limpia tokens)
 *  - Refresh automático del access token
 *  - Almacenamiento seguro en sessionStorage (no localStorage)
 */

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';
const AUTH_BASE = `${API_BASE}/api/v1/auth`;

// Keys de sessionStorage (se limpian al cerrar la pestaña)
const ACCESS_TOKEN_KEY = 'buses_access_token';
const REFRESH_TOKEN_KEY = 'buses_refresh_token';
const USER_KEY = 'buses_user';

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface AuthUser {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
}

export interface AuthTokens {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: AuthUser;
}

// =============================================
// ALMACENAMIENTO DE TOKENS
// =============================================

/** Guarda los tokens de autenticación en sessionStorage */
function saveTokens(tokens: AuthTokens): void {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, tokens.accessToken);
    sessionStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    sessionStorage.setItem(USER_KEY, JSON.stringify(tokens.user));
}

/** Elimina todos los tokens del storage (logout) */
function clearTokens(): void {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
}

/** Retorna el access token actual o null si no existe */
export function getAccessToken(): string | null {
    return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

/** Retorna el usuario autenticado actual o null */
export function getCurrentUser(): AuthUser | null {
    const userStr = sessionStorage.getItem(USER_KEY);
    if (!userStr) return null;
    try {
        return JSON.parse(userStr) as AuthUser;
    } catch {
        return null;
    }
}

/** Verifica si el usuario está autenticado */
export function isAuthenticated(): boolean {
    return getAccessToken() !== null;
}

// =============================================
// OPERACIONES DE AUTENTICACION
// =============================================

/**
 * Inicia sesión con email y contraseña.
 * Guarda access + refresh token en sessionStorage.
 */
export async function login(credentials: LoginCredentials): Promise<AuthUser> {
    const response = await fetch(`${AUTH_BASE}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Error de autenticación' }));
        throw new Error(error.error || 'Credenciales inválidas');
    }

    const tokens: AuthTokens = await response.json();
    saveTokens(tokens);
    return tokens.user;
}

/**
 * Cierra sesión.
 * Notifica al backend y limpia los tokens locales.
 */
export async function logout(): Promise<void> {
    const token = getAccessToken();
    if (token) {
        try {
            await fetch(`${AUTH_BASE}/logout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            });
        } catch {
            // Continuar con el logout aunque falle la petición al servidor
        }
    }
    clearTokens();
}

/**
 * Renueva el access token usando el refresh token.
 * Retorna el nuevo access token o lanza error si el refresh token expiró.
 */
export async function refreshAccessToken(): Promise<string> {
    const refreshToken = sessionStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refreshToken) {
        clearTokens();
        throw new Error('No hay refresh token. Inicia sesión nuevamente.');
    }

    const response = await fetch(`${AUTH_BASE}/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
        clearTokens();
        throw new Error('Sesión expirada. Inicia sesión nuevamente.');
    }

    const data = await response.json();
    sessionStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken);
    return data.accessToken;
}

/**
 * Obtiene los headers de autorización para peticiones autenticadas.
 * Incluye el Bearer token si el usuario está autenticado.
 */
export function getAuthHeaders(): HeadersInit {
    const token = getAccessToken();
    if (!token) return { 'Content-Type': 'application/json' };
    return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };
}

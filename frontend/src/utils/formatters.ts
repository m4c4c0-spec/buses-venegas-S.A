/**
 * Formatea una fecha ISO a formato legible en español
 * @param isoDate - Fecha en formato ISO (yyyy-MM-dd'T'HH:mm:ss)
 * @returns Fecha formateada (ej: "15 de Octubre, 2025")
 */
export function formatDate(isoDate: string): string {
    const date = new Date(isoDate);
    const options: Intl.DateTimeFormatOptions = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    };
    return date.toLocaleDateString('es-CL', options);
}

/**
 * Formatea una fecha ISO a formato de hora
 * @param isoDate - Fecha en formato ISO
 * @returns Hora formateada (ej: "14:30")
 */
export function formatTime(isoDate: string): string {
    const date = new Date(isoDate);
    return date.toLocaleTimeString('es-CL', {
        hour: '2-digit',
        minute: '2-digit',
    });
}

/**
 * Formatea una fecha ISO a formato completo
 * @param isoDate - Fecha en formato ISO
 * @returns Fecha y hora formateadas (ej: "15 de Octubre, 2025 - 14:30")
 */
export function formatDateTime(isoDate: string): string {
    return `${formatDate(isoDate)} - ${formatTime(isoDate)}`;
}

/**
 * Formatea un monto en pesos chilenos
 * @param amount - Monto numérico
 * @returns Monto formateado (ej: "$12.500")
 */
export function formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'CLP',
        minimumFractionDigits: 0,
    }).format(amount);
}

/**
 * Formatea un RUT chileno
 * @param rut - RUT sin formato (ej: "123456789")
 * @returns RUT formateado (ej: "12.345.678-9")
 */
export function formatRut(rut: string): string {
    // Eliminar puntos y guiones existentes
    const cleanRut = rut.replace(/\./g, '').replace(/-/g, '');

    if (cleanRut.length < 2) return cleanRut;

    // Separar dígito verificador
    const dv = cleanRut.slice(-1);
    const numbers = cleanRut.slice(0, -1);

    // Agregar puntos cada 3 dígitos
    const formattedNumbers = numbers.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    return `${formattedNumbers}-${dv}`;
}

/**
 * Valida el formato de un RUT chileno
 * @param rut - RUT a validar
 * @returns true si el RUT es válido
 */
export function validateRut(rut: string): boolean {
    const cleanRut = rut.replace(/\./g, '').replace(/-/g, '');

    if (cleanRut.length < 2) return false;

    const dv = cleanRut.slice(-1).toUpperCase();
    const numbers = cleanRut.slice(0, -1);

    if (!/^\d+$/.test(numbers)) return false;

    let sum = 0;
    let multiplier = 2;

    for (let i = numbers.length - 1; i >= 0; i--) {
        sum += parseInt(numbers[i]) * multiplier;
        multiplier = multiplier === 7 ? 2 : multiplier + 1;
    }

    const expectedDv = 11 - (sum % 11);
    const calculatedDv = expectedDv === 11 ? '0' : expectedDv === 10 ? 'K' : expectedDv.toString();

    return dv === calculatedDv;
}

/**
 * Convierte una fecha de input HTML a formato ISO para el backend
 * @param dateString - Fecha del input (yyyy-MM-dd)
 * @returns Fecha en formato ISO completo
 */
export function toISODate(dateString: string): string {
    return `${dateString}T00:00:00`;
}

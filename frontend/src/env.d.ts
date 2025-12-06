/// <reference types="vite/client" />

declare module '*.vue' {
    import type { DefineComponent } from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
}

declare module 'jspdf-autotable' {
    import { jsPDF } from 'jspdf';
    export function autoTable(doc: jsPDF, options: any): void;
    export default function autoTable(doc: jsPDF, options: any): void;
}

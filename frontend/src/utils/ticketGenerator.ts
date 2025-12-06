import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import type { BookingResponse } from '../types/booking';
import { formatDate, formatCurrency } from './formatters';

export const generateTicketPDF = (booking: BookingResponse): void => {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.width;

    // Header
    doc.setFillColor(26, 54, 93); // Dark Blue
    doc.rect(0, 0, pageWidth, 40, 'F');

    doc.setTextColor(255, 255, 255);
    doc.setFontSize(22);
    doc.setFont('helvetica', 'bold');
    doc.text('Buses Venegas S.A.', 20, 20);

    doc.setFontSize(12);
    doc.setFont('helvetica', 'normal');
    doc.text('Comprobante de Reserva', 20, 30);
    doc.text(`N°: ${booking.paymentReference || booking.id}`, pageWidth - 60, 25);

    // Trip Details
    doc.setTextColor(0, 0, 0);
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.text('Detalles del Viaje', 20, 55);

    const trip = booking.trip;
    if (trip) {
        autoTable(doc, {
            startY: 60,
            head: [['Origen', 'Destino', 'Fecha', 'Hora Salida', 'Bus']],
            body: [[
                trip.origin,
                trip.destination,
                formatDate(trip.departureTime),
                new Date(trip.departureTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                trip.busInfo || 'Bus Standard'
            ]],
            theme: 'striped',
            headStyles: { fillColor: [26, 54, 93] }
        });
    }

    // Passengers
    const currentY = (doc as any).lastAutoTable.finalY + 15;
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.text('Pasajeros y Asientos', 20, currentY);

    const passengerRows = booking.passengers.map((p, index) => [
        booking.seats[index] || '-',
        `${p.firstName} ${p.lastName}`,
        `${p.documentType} ${p.documentNumber}`
    ]);

    autoTable(doc, {
        startY: currentY + 5,
        head: [['Asiento', 'Nombre Pasajero', 'Documento']],
        body: passengerRows,
        theme: 'grid',
        headStyles: { fillColor: [44, 82, 130] }
    });

    // Payment Info
    const paymentY = (doc as any).lastAutoTable.finalY + 15;
    doc.setFontSize(14);
    doc.text('Resumen de Pago', 20, paymentY);

    autoTable(doc, {
        startY: paymentY + 5,
        body: [
            ['Estado', booking.status],
            ['Total Pagado', formatCurrency(booking.totalAmount)]
        ],
        theme: 'plain',
        columnStyles: {
            0: { fontStyle: 'bold', cellWidth: 40 },
            1: { cellWidth: 60 }
        }
    });

    // Footer
    const pageHeight = doc.internal.pageSize.height;
    doc.setFontSize(10);
    doc.setTextColor(100, 100, 100);
    doc.text('Gracias por preferir Buses Venegas S.A.', 20, pageHeight - 20);
    doc.text('Presente este ticket al momento de abordar.', 20, pageHeight - 15);

    // Save
    doc.save(`ticket_buses_venegas_${booking.id}.pdf`);
};

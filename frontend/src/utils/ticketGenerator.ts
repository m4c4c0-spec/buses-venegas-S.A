import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import type { BookingResponse } from '../types/booking';
import { formatDate, formatCurrency } from './formatters';

export const generateTicketPDF = (booking: BookingResponse): void => {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.width;
    const pageHeight = doc.internal.pageSize.height;

    // --- PAGE 1: RECEIPT SUMMARY ---

    // Header background
    doc.setFillColor(26, 54, 93); // Dark Blue
    doc.rect(0, 0, pageWidth, 40, 'F');

    // Title
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(22);
    doc.setFont('helvetica', 'bold');
    doc.text('Buses Venegas S.A.', 20, 20);

    doc.setFontSize(12);
    doc.setFont('helvetica', 'normal');
    doc.text('Comprobante de Reserva', 20, 30);
    // Booking ID
    doc.text(`Reserva N°: ${booking.id}`, pageWidth - 70, 20);
    doc.text(`Ref: ${booking.paymentReference || 'PENDING'}`, pageWidth - 70, 30);

    // Trip Details Section
    doc.setTextColor(0, 0, 0);
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.text('Detalles del Viaje', 20, 55);

    const trip = booking.trip;
    if (trip) {
        autoTable(doc, {
            startY: 60,
            head: [['Origen', 'Destino', 'Fecha', 'Hora', 'Bus']],
            body: [[
                trip.origin,
                trip.destination,
                formatDate(trip.departureTime),
                new Date(trip.departureTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                trip.busInfo || 'Semi-Cama'
            ]],
            theme: 'striped',
            headStyles: { fillColor: [26, 54, 93] }
        });
    }

    // Passengers Table (Summary)
    const currentY = (doc as any).lastAutoTable.finalY + 15;
    doc.setFontSize(14);
    doc.text('Lista de Pasajeros', 20, currentY);

    const passengerRows = booking.passengers.map((p, index) => [
        booking.seats[index] || '-',
        `${p.firstName} ${p.lastName}`,
        `${p.documentType} ${p.documentNumber}`
    ]);

    autoTable(doc, {
        startY: currentY + 5,
        head: [['Asiento', 'Nombre', 'Documento']],
        body: passengerRows,
        theme: 'grid',
        headStyles: { fillColor: [44, 82, 130] }
    });

    // Totals
    const paymentY = (doc as any).lastAutoTable.finalY + 15;
    autoTable(doc, {
        startY: paymentY,
        body: [
            ['Total Pagado', formatCurrency(booking.totalAmount)]
        ],
        theme: 'plain',
        columnStyles: {
            0: { fontStyle: 'bold', fontSize: 12, cellWidth: 40 },
            1: { fontSize: 12 }
        }
    });

    // Footer Page 1
    doc.setFontSize(10);
    doc.setTextColor(100, 100, 100);
    doc.text('Este documento es un comprobante de compra.', 20, pageHeight - 10);


    // --- PAGES 2+: BOARDING PASSES ---

    booking.passengers.forEach((p, index) => {
        doc.addPage();

        // Boarding Pass Header
        doc.setFillColor(26, 54, 93);
        doc.rect(0, 0, pageWidth, 30, 'F');

        doc.setTextColor(255, 255, 255);
        doc.setFontSize(18);
        doc.setFont('helvetica', 'bold');
        doc.text('TARJETA DE EMBARQUE', 20, 20);
        doc.setFontSize(10);
        doc.text('Buses Venegas S.A.', pageWidth - 60, 20);

        // Seat Number (Big)
        doc.setFillColor(255, 235, 59); // Yellow
        doc.circle(pageWidth - 30, 50, 15, 'F');
        doc.setTextColor(26, 54, 93);
        doc.setFontSize(16);
        doc.text(booking.seats[index] || '?', pageWidth - 34, 52);
        doc.setFontSize(8);
        doc.text('ASIENTO', pageWidth - 36, 62);

        // Passenger Name
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(12);
        doc.text('PASAJERO', 20, 50);
        doc.setFontSize(22);
        doc.text(`${p.firstName} ${p.lastName}`.toUpperCase(), 20, 60);

        // Line Separator
        doc.setDrawColor(200, 200, 200);
        doc.line(20, 70, pageWidth - 20, 70);

        // Trip Info Grid
        doc.setFontSize(10);
        doc.setTextColor(100, 100, 100);
        doc.text('ORIGEN', 20, 85);
        doc.text('DESTINO', 80, 85);
        doc.text('FECHA', 140, 85);

        doc.setFontSize(14);
        doc.setTextColor(0, 0, 0);
        doc.text(trip?.origin || '', 20, 95);
        doc.text(trip?.destination || '', 80, 95);
        doc.text(formatDate(trip?.departureTime || ''), 140, 95);

        doc.setFontSize(10);
        doc.setTextColor(100, 100, 100);
        doc.text('HORA', 140, 110);
        doc.setFontSize(14);
        doc.setTextColor(0, 0, 0);
        doc.text(new Date(trip?.departureTime || '').toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }), 140, 120);

        // Fake QR Code
        doc.setFillColor(0, 0, 0);
        doc.rect(20, 140, 40, 40, 'F');
        doc.setFillColor(255, 255, 255);
        doc.rect(25, 145, 30, 30, 'F'); // Inner white
        doc.setFillColor(0, 0, 0);
        doc.rect(30, 150, 20, 20, 'F'); // Inner black

        doc.setFontSize(8);
        doc.text('Escanear al abordar', 20, 185);
    });

    // Save
    doc.save(`tickets_buses_venegas_${booking.id}.pdf`);
};

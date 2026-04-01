package cl.venegas.buses_api.application.usecase.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @SuppressWarnings("unchecked")
    public void sendReceiptEmail(Map<String, Object> detalles) {
        try {
            List<Map<String, Object>> pasajerosData = (List<Map<String, Object>>) detalles.get("pasajerosData");
            if (pasajerosData == null || pasajerosData.isEmpty()) return;

            String origen = String.valueOf(detalles.getOrDefault("origen", ""));
            String destino = String.valueOf(detalles.getOrDefault("destino", ""));
            String fechaIda = String.valueOf(detalles.getOrDefault("fechaIda", ""));
            String horario = detalles.get("horarioViaje") != null ? String.valueOf(((Map<String, Object>)detalles.get("horarioViaje")).getOrDefault("horaSalida", "")) : "";
            String precioTotal = String.valueOf(detalles.getOrDefault("precioTotal", "0"));
            String numReserva = String.valueOf((int)(Math.random() * 900000) + 100000);

            // Send email to each passenger
            for (Map<String, Object> pasajero : pasajerosData) {
                String email = (String) pasajero.get("email");
                String nombre = (String) pasajero.get("nombre");
                String apellidos = (String) pasajero.getOrDefault("apellidos", "");
                String rut = (String) pasajero.getOrDefault("rut", "");
                String asiento = String.valueOf(pasajero.getOrDefault("asiento", ""));

                if (email == null || email.isEmpty()) continue;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(mailUsername, "Buses Venegas S.A.");
                helper.setTo(email);
                helper.setSubject("✅ Confirmación de Pasaje #" + numReserva + " - Buses Venegas S.A.");

                String ticketHash = (String) detalles.get("ticketHash");
                String htmlBody = buildEmailHtml(nombre, apellidos, rut, asiento, origen, destino, fechaIda, horario, precioTotal, numReserva, ticketHash);
                helper.setText(htmlBody, true);

                mailSender.send(message);
                System.out.println("✅ Correo enviado exitosamente a: " + email);
            }
        } catch (Exception e) {
            System.err.println("❌ Error enviando el correo de confirmación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendChangeReceiptEmail(cl.venegas.buses_api.domain.model.entity.Reserva reserva) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> pasajeros = mapper.readValue(
                reserva.getPasajerosJson(), 
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {}
            );
            
            for (Map<String, Object> pasajero : pasajeros) {
                String email = (String) pasajero.get("email");
                String nombre = (String) pasajero.get("nombre");
                String apellidos = (String) pasajero.getOrDefault("apellidos", "");
                String rut = (String) pasajero.getOrDefault("rut", "");
                String asiento = String.valueOf(pasajero.getOrDefault("asiento", ""));

                if (email == null || email.isEmpty()) continue;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(mailUsername, "Buses Venegas S.A.");
                helper.setTo(email);
                helper.setSubject("🔄 Actualización de Pasaje #" + reserva.getId() + " - Buses Venegas S.A.");

                String htmlBody = buildEmailHtml(nombre, apellidos, rut, asiento, reserva.getOrigen(), reserva.getDestino(), reserva.getFechaViaje(), reserva.getHorarioSalida(), String.valueOf(reserva.getPrecioTotal()), reserva.getId(), null);
                helper.setText(htmlBody, true);

                mailSender.send(message);
                System.out.println("✅ Correo de cambio enviado exitosamente a: " + email);
            }
        } catch (Exception e) {
            System.err.println("❌ Error enviando el correo de cambio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendCancellationEmail(cl.venegas.buses_api.domain.model.entity.Reserva reserva) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> pasajeros = mapper.readValue(
                reserva.getPasajerosJson(), 
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {}
            );
            
            for (Map<String, Object> pasajero : pasajeros) {
                String email = (String) pasajero.get("email");
                String nombre = (String) pasajero.get("nombre");
                String apellidos = (String) pasajero.getOrDefault("apellidos", "");

                if (email == null || email.isEmpty()) continue;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(mailUsername, "Buses Venegas S.A.");
                helper.setTo(email);
                helper.setSubject("⚠️ Anulación de Pasaje #" + reserva.getId() + " Confirmada");

                String htmlBody = "<!DOCTYPE html><html><head><meta charset='UTF-8'></head>"
                    + "<body style='margin:0; padding:0; font-family:Arial,sans-serif; background-color:#f4f4f4;'>"
                    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding:30px 0;'>"
                    + "<tr><td align='center'>"
                    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 15px rgba(0,0,0,0.1);'>"
                    + "<tr><td style='background: linear-gradient(135deg, #d32f2f 0%, #f44336 100%); padding:30px; text-align:center;'>"
                    + "<h1 style='color:#ffffff; margin:0; font-size:24px;'>Pasaje Anulado Exitosamente</h1>"
                    + "</td></tr>"
                    + "<tr><td style='padding:30px;'>"
                    + "<p style='color:#333; font-size:16px;'>Estimado/a <strong>" + nombre + " " + apellidos + "</strong>,</p>"
                    + "<p style='color:#555; line-height:1.6;'>Te confirmamos que hemos procesado la anulación de tu reserva <strong>#" + reserva.getId() + "</strong> (Origen: " + reserva.getOrigen() + " - Destino: " + reserva.getDestino() + ").</p>"
                    + "<p style='color:#555; line-height:1.6;'>De acuerdo a nuestras políticas de anulación, si tienes derecho a un reembolso porcentual basado en las horas de anticipación, este será procesado hacia el método de devolución en <strong style='color:#d32f2f;'>5 a 10 días hábiles</strong>.</p>"
                    + "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'>"
                    + "<p style='color:#888; font-size:12px; text-align:center;'>Gracias por preferir Buses Venegas S.A., ¡esperamos verte pronto en un próximo viaje!</p>"
                    + "</td></tr>"
                    + "</table></td></tr></table></body></html>";

                helper.setText(htmlBody, true);
                mailSender.send(message);
                System.out.println("✅ Correo de anulación enviado exitosamente a: " + email);
            }
        } catch (Exception e) {
            System.err.println("❌ Error enviando el correo de anulación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildEmailHtml(String nombre, String apellidos, String rut, String asiento,
                                   String origen, String destino, String fecha, String horario,
                                   String precioTotal, String numReserva, String ticketHash) {
        String bodyHtml = "<!DOCTYPE html>"
            + "<html><head><meta charset='UTF-8'></head>"
            + "<body style='margin:0; padding:0; background-color:#f4f4f4; font-family:Arial,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:30px 0;'>"
            + "<tr><td align='center'>"
            + "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 15px rgba(0,0,0,0.1);'>"

            // Header
            + "<tr><td style='background: linear-gradient(135deg, #0d286d 0%, #174291 100%); padding:30px; text-align:center;'>"
            + "<h1 style='color:#ffffff; margin:0; font-size:24px;'>Buses Venegas S.A.</h1>"
            + "<p style='color:#ffeb3b; margin:8px 0 0; font-size:14px;'>Acercamos a nuestra gente, uniendo regiones</p>"
            + "</td></tr>"

            // Success banner
            + "<tr><td style='background-color:#d4edda; padding:20px; text-align:center; border-bottom:2px solid #c3e6cb;'>"
            + "<h2 style='color:#155724; margin:0; font-size:20px;'>✅ ¡Pago Confirmado!</h2>"
            + "<p style='color:#155724; margin:5px 0 0; font-size:14px;'>Reserva N° <strong>" + numReserva + "</strong></p>"
            + "</td></tr>"

            // Greeting
            + "<tr><td style='padding:25px 30px 10px;'>"
            + "<p style='color:#333; font-size:16px; margin:0;'>Estimado/a <strong>" + nombre + " " + apellidos + "</strong>,</p>"
            + "<p style='color:#666; font-size:14px; margin:10px 0 0;'>Tu pasaje ha sido confirmado exitosamente. A continuación los detalles de tu viaje:</p>"
            + "</td></tr>"

            // Trip details
            + "<tr><td style='padding:15px 30px;'>"
            + "<table width='100%' cellpadding='12' cellspacing='0' style='background-color:#f8f9fa; border-radius:8px; border:1px solid #e9ecef;'>"
            + "<tr>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#6c757d; font-size:12px; text-transform:uppercase;'>Origen</td>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#6c757d; font-size:12px; text-transform:uppercase;'>Destino</td>"
            + "</tr>"
            + "<tr>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#0d286d; font-size:18px; font-weight:bold;'>" + origen + "</td>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#0d286d; font-size:18px; font-weight:bold;'>" + destino + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#6c757d; font-size:12px; text-transform:uppercase;'>Fecha</td>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#6c757d; font-size:12px; text-transform:uppercase;'>Horario</td>"
            + "</tr>"
            + "<tr>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#333; font-size:15px; font-weight:bold;'>" + fecha + "</td>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#333; font-size:15px; font-weight:bold;'>" + (horario.isEmpty() ? "Ver boleto" : horario) + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#6c757d; font-size:12px; text-transform:uppercase;'>Asiento</td>"
            + "<td style='border-bottom:1px solid #e9ecef; color:#6c757d; font-size:12px; text-transform:uppercase;'>RUT</td>"
            + "</tr>"
            + "<tr>"
            + "<td style='color:#333; font-size:15px; font-weight:bold;'>N° " + asiento + "</td>"
            + "<td style='color:#333; font-size:15px;'>" + rut + "</td>"
            + "</tr>"
            + "</table>"
            + "</td></tr>"

            // Total
            + "<tr><td style='padding:15px 30px;'>"
            + "<table width='100%' cellpadding='15' cellspacing='0' style='background-color:#0d286d; border-radius:8px;'>"
            + "<tr>"
            + "<td style='color:#ffffff; font-size:14px;'>Total Pagado</td>"
            + "<td align='right' style='color:#ffeb3b; font-size:22px; font-weight:bold;'>$" + precioTotal + " CLP</td>"
            + "</tr>"
            + "</table>"
            + "</td></tr>";

        // Blockchain section if available
        if (ticketHash != null && !ticketHash.isEmpty()) {
            bodyHtml += "<tr><td style='padding:15px 30px;'>"
                + "<table width='100%' cellpadding='15' cellspacing='0' style='background-color:#f8f9fa; border-radius:8px; border: 1px dashed #6f42c1;'>"
                + "<tr>"
                + "<td width='50'><img src='https://cdn-icons-png.flaticon.com/512/2152/2152349.png' width='40' /></td>"
                + "<td>"
                + "<h4 style='color:#333; margin:0; font-size:14px;'>🛡️ Verificado en Blockchain</h4>"
                + "<p style='color:#666; margin:5px 0 0; font-size:11px;'>Hash Inmutable: <code style='background:#eee; padding:2px 4px; border-radius:3px;'>" + ticketHash + "</code></p>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</td></tr>";
        }

        bodyHtml += "<tr><td style='padding:20px 30px;'>"
            + "<div style='background-color:#fff3cd; border-left:4px solid #ffc107; padding:15px; border-radius:4px;'>"
            + "<p style='margin:0; color:#856404; font-size:13px;'><strong>📋 Importante:</strong> Presenta este correo electrónico (impreso o en tu celular) al momento de abordar el bus junto con tu documento de identidad.</p>"
            + "</div>"
            + "</td></tr>"
            + "<tr><td style='background-color:#1a1a1a; padding:25px 30px; text-align:center;'>"
            + "<p style='color:#aaa; font-size:12px; margin:0;'>Buses Venegas S.A. — Conectando a nuestra gente desde 1995</p>"
            + "<p style='color:#666; font-size:11px; margin:8px 0 0;'>📞 +56 9 8765 4321 | ✉️ contacto@busesvenegas.cl</p>"
            + "<p style='color:#555; font-size:10px; margin:10px 0 0;'>Este correo fue generado automáticamente. No responder.</p>"
            + "</td></tr>"
            + "</table></td></tr></table></body></html>";
        
        return bodyHtml;
    }
}

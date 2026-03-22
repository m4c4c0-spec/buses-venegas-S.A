package cl.venegas.buses_api.application.usecase.email;

import org.springframework.beans.factory.annotation.Autowired;
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

                helper.setFrom("busesvenegassa@gmail.com", "Buses Venegas S.A.");
                helper.setTo(email);
                helper.setSubject("✅ Confirmación de Pasaje #" + numReserva + " - Buses Venegas S.A.");

                String htmlBody = buildEmailHtml(nombre, apellidos, rut, asiento, origen, destino, fechaIda, horario, precioTotal, numReserva);
                helper.setText(htmlBody, true);

                mailSender.send(message);
                System.out.println("✅ Correo enviado exitosamente a: " + email);
            }
        } catch (Exception e) {
            System.err.println("❌ Error enviando el correo de confirmación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildEmailHtml(String nombre, String apellidos, String rut, String asiento,
                                   String origen, String destino, String fecha, String horario,
                                   String precioTotal, String numReserva) {
        return "<!DOCTYPE html>"
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
            + "</td></tr>"

            // Instructions
            + "<tr><td style='padding:20px 30px;'>"
            + "<div style='background-color:#fff3cd; border-left:4px solid #ffc107; padding:15px; border-radius:4px;'>"
            + "<p style='margin:0; color:#856404; font-size:13px;'><strong>📋 Importante:</strong> Presenta este correo electrónico (impreso o en tu celular) al momento de abordar el bus junto con tu documento de identidad.</p>"
            + "</div>"
            + "</td></tr>"

            // Footer
            + "<tr><td style='background-color:#1a1a1a; padding:25px 30px; text-align:center;'>"
            + "<p style='color:#aaa; font-size:12px; margin:0;'>Buses Venegas S.A. — Conectando a nuestra gente desde 1995</p>"
            + "<p style='color:#666; font-size:11px; margin:8px 0 0;'>📞 +56 9 8765 4321 | ✉️ contacto@busesvenegas.cl</p>"
            + "<p style='color:#555; font-size:10px; margin:10px 0 0;'>Este correo fue generado automáticamente. No responder.</p>"
            + "</td></tr>"

            + "</table></td></tr></table></body></html>";
    }
}

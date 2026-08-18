package com.atalaya.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//cambio final agregado
@Service
public class CorreoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorreoService.class);
    private static final URI MAILERSEND_EMAIL_URI =
            URI.create("https://api.mailersend.com/v1/email");
    private static final URI GOOGLE_TOKEN_URI =
            URI.create("https://oauth2.googleapis.com/token");
    private static final URI GMAIL_SEND_URI =
            URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send");

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final JavaMailSender mailSender;
    private final String proveedor;
    private final String apiToken;
    private final String remitente;
    private final String gmailClientId;
    private final String gmailClientSecret;
    private final String gmailRefreshToken;

    public CorreoService(ObjectMapper objectMapper,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${correo.proveedor:mailersend}") String proveedor,
            @Value("${mailersend.api-token:}") String apiToken,
            @Value("${correo.remitente:}") String remitente,
            @Value("${gmail.client-id:}") String gmailClientId,
            @Value("${gmail.client-secret:}") String gmailClientSecret,
            @Value("${gmail.refresh-token:}") String gmailRefreshToken) {
        this.objectMapper = objectMapper;
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.proveedor = proveedor;
        this.apiToken = apiToken;
        this.remitente = remitente;
        this.gmailClientId = gmailClientId;
        this.gmailClientSecret = gmailClientSecret;
        this.gmailRefreshToken = gmailRefreshToken;
    }

    //cambio final agregado
    @Async("correoExecutor")
    public void enviarCorreoHtml(String para,
            String asunto,
            String contenido) {

        if (remitente.isBlank()) {
            LOGGER.warn("No se envió el correo a {}: falta configurar el remitente", para);
            return;
        }

        switch (proveedor.toLowerCase()) {
            case "gmail", "gmail-smtp" -> enviarConGmailSmtp(para, asunto, contenido);
            case "gmail-api" -> enviarConGmailApi(para, asunto, contenido);
            default -> enviarConMailerSend(para, asunto, contenido);
        }
    }

    private void enviarConGmailSmtp(String para, String asunto, String contenido) {
        if (mailSender == null) {
            LOGGER.warn("No se envió el correo a {}: Gmail SMTP no está configurado", para);
            return;
        }

        try {
            mailSender.send(crearMensaje(para, asunto, contenido));
        } catch (Exception e) {
            LOGGER.warn("Gmail SMTP no pudo enviar el correo a {}: {}", para, e.getMessage());
        }
    }

    private void enviarConGmailApi(String para, String asunto, String contenido) {
        if (gmailClientId.isBlank() || gmailClientSecret.isBlank()
                || gmailRefreshToken.isBlank()) {
            LOGGER.warn("No se envió el correo a {}: faltan credenciales de Gmail API", para);
            return;
        }

        try {
            String accessToken = obtenerAccessToken();
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            crearMensaje(para, asunto, contenido).writeTo(salida);
            String raw = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(salida.toByteArray());
            String cuerpo = objectMapper.writeValueAsString(Map.of("raw", raw));

            HttpRequest solicitud = HttpRequest.newBuilder(GMAIL_SEND_URI)
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                    .build();
            HttpResponse<String> respuesta = httpClient.send(
                    solicitud, HttpResponse.BodyHandlers.ofString());

            if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {
                LOGGER.warn("Gmail API rechazó el correo a {} (HTTP {}): {}",
                        para, respuesta.statusCode(), respuesta.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Se interrumpió el envío por Gmail API a {}", para);
        } catch (Exception e) {
            LOGGER.warn("Gmail API no pudo enviar el correo a {}: {}", para, e.getMessage());
        }
    }

    private String obtenerAccessToken() throws IOException, InterruptedException {
        String formulario = "client_id=" + codificar(gmailClientId)
                + "&client_secret=" + codificar(gmailClientSecret)
                + "&refresh_token=" + codificar(gmailRefreshToken)
                + "&grant_type=refresh_token";
        HttpRequest solicitud = HttpRequest.newBuilder(GOOGLE_TOKEN_URI)
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formulario))
                .build();
        HttpResponse<String> respuesta = httpClient.send(
                solicitud, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {
            throw new IOException("Google OAuth rechazó la renovación (HTTP "
                    + respuesta.statusCode() + "): " + respuesta.body());
        }

        String accessToken = objectMapper.readTree(respuesta.body())
                .path("access_token").asText();
        if (accessToken.isBlank()) {
            throw new IOException("Google OAuth no devolvió access_token");
        }
        return accessToken;
    }

    private String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private MimeMessage crearMensaje(String para, String asunto, String contenido)
            throws Exception {
        MimeMessage mensaje = mailSender != null
                ? mailSender.createMimeMessage()
                : new MimeMessage(Session.getInstance(new Properties()));
        MimeMessageHelper correo = new MimeMessageHelper(mensaje, true, "UTF-8");
        correo.setFrom(remitente);
        correo.setTo(para);
        correo.setSubject(asunto);
        correo.setText(contenido, true);
        return mensaje;
    }

    private void enviarConMailerSend(String para, String asunto, String contenido) {
        if (apiToken.isBlank()) {
            LOGGER.warn("No se envió el correo a {}: falta MAILERSEND_API_TOKEN", para);
            return;
        }

        try {
            HttpRequest solicitud = HttpRequest.newBuilder(MAILERSEND_EMAIL_URI)
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            crearCuerpo(para, asunto, contenido)))
                    .build();

            HttpResponse<String> respuesta = httpClient.send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {
                LOGGER.warn("MailerSend rechazó el correo a {} (HTTP {}): {}",
                        para, respuesta.statusCode(), respuesta.body());
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("No se pudo construir el correo para {}", para, e);
        } catch (IOException e) {
            LOGGER.warn("No se pudo conectar con MailerSend para enviar a {}: {}",
                    para, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Se interrumpió el envío del correo a {}", para);
        }
    }

    private String crearCuerpo(String para, String asunto, String contenido)
            throws JsonProcessingException {
        Map<String, Object> cuerpo = Map.of(
                "from", Map.of("email", remitente, "name", "Atalaya"),
                "to", List.of(Map.of("email", para)),
                "subject", asunto,
                "html", contenido
        );
        return objectMapper.writeValueAsString(cuerpo);
    }
}

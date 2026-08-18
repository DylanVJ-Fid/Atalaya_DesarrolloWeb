package com.atalaya.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//cambio final agregado
@Service
public class CorreoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorreoService.class);
    private static final URI MAILERSEND_EMAIL_URI =
            URI.create("https://api.mailersend.com/v1/email");

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiToken;
    private final String remitente;

    public CorreoService(ObjectMapper objectMapper,
            @Value("${mailersend.api-token:}") String apiToken,
            @Value("${mailersend.from:}") String remitente) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.apiToken = apiToken;
        this.remitente = remitente;
    }

    //cambio final agregado
    @Async("correoExecutor")
    public void enviarCorreoHtml(String para,
            String asunto,
            String contenido) {

        if (apiToken.isBlank() || remitente.isBlank()) {
            LOGGER.warn("No se envió el correo a {}: faltan MAILERSEND_API_TOKEN o MAIL_FROM", para);
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

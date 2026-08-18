package com.atalaya.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//cambio final agregado
@Service
public class CorreoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorreoService.class);

    private final JavaMailSender mailSender;
    private final String remitente;

    public CorreoService(JavaMailSender mailSender,
            @Value("${spring.mail.from}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    //cambio final agregado
    @Async("correoExecutor")
    public void enviarCorreoHtml(String para,
            String asunto,
            String contenido) {

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper correo = new MimeMessageHelper(mensaje, true);

            correo.setFrom(remitente);
            correo.setTo(para);
            correo.setSubject(asunto);
            correo.setText(contenido, true);

            mailSender.send(mensaje);
        } catch (Exception e) {
            LOGGER.warn("No se pudo enviar el correo de activación a {}: {}", para, e.getMessage());
        }
    }
}

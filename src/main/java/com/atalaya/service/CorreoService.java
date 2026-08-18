package com.atalaya.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//cambio final agregado
@Service
public class CorreoService {

    private final JavaMailSender mailSender;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //cambio final agregado
    @Async("correoExecutor")
    public void enviarCorreoHtml(String para,
            String asunto,
            String contenido) {

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper correo = new MimeMessageHelper(mensaje, true);

            correo.setTo(para);
            correo.setSubject(asunto);
            correo.setText(contenido, true);

            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new MailPreparationException("No se pudo preparar el correo de activación", e);
        }
    }
}
